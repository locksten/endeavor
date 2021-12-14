import { AppContext } from "context"
import { db, dc } from "database"
import { sendNotification } from "firebaseMessaging"
import { Battle } from "schema/battle"
import { Creature } from "schema/creature"
import {
  levelFromExperience,
  User,
  experienceFromLevel,
  QUser,
  maxEnergyFromLevel,
  maxHitpointsFromLevel,
} from "schema/user"
import { select } from "zapatos/db"

export const giveRewardForTodo = async (
  ctx: AppContext,
  difficulty: number,
) => {
  const { auth, pool } = ctx
  if (!auth.id) return

  await db.serializable(pool, async (txnClient) => {
    const user = await db.selectOne("User", { id: auth.id }).run(txnClient)
    if (user === undefined) return

    const level = levelFromExperience(user.experience)
    const rewardAmount = Math.round(difficulty / 20) * (1 + level / 10)

    const experienceReward = rewardAmount

    const newExperience = user.experience + Math.round(experienceReward)
    const newLevel = levelFromExperience(newExperience)
    const leveledUp = level != newLevel

    const newMaxEnergy = maxEnergyFromLevel(newLevel)
    const newMaxHitpoints = maxHitpointsFromLevel(newLevel)

    const hitpointsReward = Math.max(1, rewardAmount / 2)
    const energyReward = Math.max(1, rewardAmount / 5)
    const goldReward = Math.max(1, rewardAmount)

    const damage = Math.round(Math.max(1, rewardAmount / 2))

    const battle = (
      await db
        .update(
          "Battle",
          { creatureHitpoints: dc.subtract(damage) },
          { partyLeaderId: user.partyLeaderOrUserId },
        )
        .run(txnClient)
    ).at(0)
    await checkBattleVictory({ ...ctx, pool: txnClient }, user, battle)

    const newUser = (
      await db
        .update(
          "User",
          {
            maxEnergy: newMaxEnergy,
            maxHitpoints: newMaxHitpoints,
            hitpoints: Math.round(
              Math.min(
                newMaxHitpoints,
                leveledUp ? newMaxHitpoints : user.hitpoints + hitpointsReward,
              ),
            ),
            energy: Math.round(
              Math.min(
                newMaxEnergy,
                leveledUp ? newMaxEnergy : user.energy + energyReward,
              ),
            ),
            experience: newExperience,
            gold: Math.round(user.gold + goldReward),
          },
          { id: Number(auth.id) },
        )
        .run(txnClient)
    ).at(0)
    if (newUser === undefined) return

    leveledUp && levelUpEvent({ ...ctx, pool: txnClient }, newUser)
  })
}

export const givePenaltyForTodo = async (
  ctx: AppContext,
  difficulty: number,
) => {
  const { auth, pool } = ctx
  if (!auth.id) return

  await db.serializable(pool, async (txnClient) => {
    const user = await db.selectOne("User", { id: auth.id }).run(txnClient)
    if (user === undefined) return

    const level = levelFromExperience(user.experience)

    let penaltyAmount = (difficulty / 20) * (1 + level / 10)

    let creature: Creature | undefined = undefined
    const battle = await db
      .selectOne("Battle", { partyLeaderId: Number(user.partyLeaderOrUserId) })
      .run(txnClient)
    if (battle !== undefined) {
      creature = await db
        .selectOne("Creature", { id: Number(battle.creatureId) })
        .run(txnClient)
      if (creature !== undefined)
        penaltyAmount += (difficulty / 20) * (1 + creature.strength / 20)
    }

    const hitpointsPenalty = Math.max(1, penaltyAmount / 2)

    const newHitpoints = Math.max(
      0,
      Math.round(user.hitpoints - hitpointsPenalty),
    )

    if (newHitpoints == 0) {
      die({ ...ctx, pool: txnClient }, user, creature)
    } else {
      await db
        .update(
          "User",
          {
            hitpoints: newHitpoints,
          },
          { id: Number(auth.id) },
        )
        .run(txnClient)
    }
  })
}

const die = async (
  { auth, pool }: AppContext,
  user: User,
  creature?: Creature,
) => {
  await db
    .update(
      "User",
      {
        experience: experienceFromLevel(levelFromExperience(user.experience)),
        hitpoints: db.sql<QUser.SQL>`${"maxHitpoints"} / 2`,
        energy: 0,
      },
      { id: Number(auth.id) },
    )
    .run(pool)

  if (!user?.firebaseToken) return
  if (creature === undefined) return

  const payload = {
    notification: {
      title: "Defeat!",
      body: `You were defeated by a ${creature.name} ${creature.emoji} !`,
    },
  }

  user.firebaseToken && sendNotification(user.firebaseToken, payload)
}

export const checkBattleVictory = async (
  ctx: AppContext,
  user: User,
  battle: Battle | undefined,
) => {
  if (battle === undefined) return
  if (battle.creatureHitpoints > 0) return
  await battleVictory(ctx, battle, user)
}

const battleVictory = async (ctx: AppContext, battle: Battle, user: User) => {
  const { pool } = ctx

  await db
    .deletes("Battle", {
      partyLeaderId: Number(user.partyLeaderOrUserId),
    })
    .run(pool)

  const creature = await db
    .selectOne("Creature", { id: battle.creatureId })
    .run(pool)
  if (creature === undefined) return

  const tokens = await getPartysFirebaseTokens(ctx, user.partyLeaderOrUserId)

  const message = {
    notification: {
      title: "Victory!",
      body: `You defeated a ${creature.name} ${creature.emoji} !`,
    },
  }
  sendNotification(tokens, message)

  battleVictoryRecordStat(ctx, user, creature)
  battleVictoryReward(ctx, user, battle, creature)
}

const battleVictoryRecordStat = async (
  { pool }: AppContext,
  user: User,
  creature: Creature,
) => {
  const userCreature = await db
    .selectOne("UserCreature", { userId: user.id, creatureId: creature.id })
    .run(pool)

  console.log("userCreature", userCreature)
  if (!userCreature) {
    const k = await db
      .insert("UserCreature", { userId: user.id, creatureId: creature.id })
      .run(pool)
    console.log(k)
  }
  const v = await db
    .update(
      "UserCreature",
      { victoryCount: dc.add(1) },
      { userId: user.id, creatureId: creature.id },
    )
    .run(pool)
  console.log("victory logged", v)
}

const battleVictoryReward = async (
  ctx: AppContext,
  user: User,
  _battle: Battle,
  creature: Creature,
) => {
  const { pool } = ctx
  const goldReward = creature.strength * 2

  const newUser = (
    await db
      .update(
        "User",
        { gold: dc.add(goldReward) },
        { partyLeaderOrUserId: user.partyLeaderOrUserId },
      )
      .run(pool)
  ).at(0)
  if (newUser === undefined) return

  const itemReward = await getRandomItem(ctx)
  console.log("itemReward", itemReward)
  if (itemReward === undefined) return

  let isRewardItemDuplicate = false
  try {
    const userItem = await db
      .insert("UserItem", { itemId: itemReward.id, userId: newUser.id })
      .run(pool)
    console.log("userItem", userItem)
  } catch (e) {
    console.log("EEE", e)
    if (db.isDatabaseError(e, "IntegrityConstraintViolation_UniqueViolation")) {
      isRewardItemDuplicate = true
    }
  }

  const message = {
    notification: {
      title: "Victory!",
      body: `You ${isRewardItemDuplicate ? "found another" : "received"} ${
        itemReward.emoji
      } ${itemReward.name} !`,
    },
  }
  const tokens = await getPartysFirebaseTokens(ctx, newUser.partyLeaderOrUserId)

  sendNotification(tokens, message)
}

const levelUpEvent = (ctx: AppContext, user: User) => {
  const payload = {
    notification: {
      title: "⬆️ Leveled Up!",
      body: `You've reached level ${levelFromExperience(user.experience)} !`,
    },
  }
  user.firebaseToken && sendNotification(user.firebaseToken, payload)
}

const getPartysFirebaseTokens = async (
  { pool }: AppContext,
  partyLeaderId: number,
) => {
  const users = await db
    .select("User", { partyLeaderOrUserId: partyLeaderId })
    .run(pool)

  const tokens = users
    .map((user) => user.firebaseToken)
    .filter((token) => token) as string[]

  return tokens.length == 0 ? undefined : tokens
}

const getRandomItemId = async ({ pool }: AppContext) => {
  const itemCount = (await db.select("Item", db.all).run(pool)).length
  return Math.floor(Math.random() * itemCount)
}

const getRandomItem = async (ctx: AppContext) => {
  const id = await getRandomItemId(ctx)
  return await db.selectOne("Item", { id }).run(ctx.pool)
}
