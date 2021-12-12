import { AppContext } from "context"
import { db, dc } from "database"
import { sendNotification } from "firebaseMessaging"
import { ObjectType } from "gqtx"
import { Battle } from "schema/battle"
import { Creature } from "schema/creature"
import { DateType } from "schema/date"
import { idResolver, t, _typeResolver } from "schema/typesFactory"
import {
  experienceFromLevel,
  levelFromExperience,
  QUser,
  User,
} from "schema/user"
import { isObjectEmpty } from "utils"
import { Reward as QReward } from "zapatos/schema"

export { Reward as QReward } from "zapatos/schema"
export type Reward = QReward.JSONSelectable

export const RewardType: ObjectType<AppContext, Reward> = t.objectType<Reward>({
  name: "Reward",
  fields: () => [
    idResolver,
    _typeResolver("Reward"),
    t.field({ name: "title", type: t.NonNull(t.String) }),
    t.field({ name: "createdAt", type: t.NonNull(DateType) }),
    t.field({ name: "price", type: t.NonNull(t.Int) }),
  ],
})

export const createRewardInput = t.inputObjectType({
  name: "CreateRewardInput",
  fields: () => ({
    title: t.arg(t.NonNullInput(t.String)),
    price: t.arg(t.NonNullInput(t.Int)),
  }),
})

export const mutationCreateReward = t.field({
  name: "createReward",
  type: RewardType,
  args: {
    createRewardInput: t.arg(t.NonNullInput(createRewardInput)),
  },
  resolve: async (_, { createRewardInput: input }, { pool, auth }) => {
    if (!auth.id) return

    if (input.price < 1) return
    const reward: QReward.Insertable = {
      userId: auth.id,
      ...input,
    }

    try {
      return await db.insert("Reward", reward).run(pool)
    } catch (e) {
      console.log(e)
    }
  },
})

export const mutationDeleteReward = t.field({
  name: "deleteReward",
  type: t.ID,
  args: {
    id: t.arg(t.NonNullInput(t.ID)),
  },
  resolve: async (_, { id }, { pool, auth }) => {
    const deletedIds = await db
      .deletes(
        "Reward",
        { userId: auth.id, id: Number(id) },
        { returning: ["id"] },
      )
      .run(pool)
    return deletedIds.at(0) !== undefined ? String(deletedIds[0].id) : undefined
  },
})

export const mutationBuyReward = t.field({
  name: "buyReward",
  type: t.ID,
  args: {
    id: t.arg(t.NonNullInput(t.ID)),
  },
  resolve: async (_, { id }, ctx) => {
    const { pool, auth } = ctx

    const reward = await db.selectOne("Reward", { id: Number(id) }).run(pool)
    if (reward === undefined) return

    const user = (
      await db
        .update(
          "User",
          { gold: db.sql`${db.self} - ${db.param(reward.price)}` },
          {
            id: Number(auth.id),
            gold: dc.gte(reward.price),
          },
        )
        .run(pool)
    ).at(0)

    if (user === undefined) return

    return String(reward.id)
  },
})

export const updateRewardInput = t.inputObjectType({
  name: "UpdateRewardInput",
  fields: () => ({
    id: t.arg(t.NonNullInput(t.ID)),
    title: t.arg(t.String),
    price: t.arg(t.Int),
  }),
})

export const mutationUpdateReward = t.field({
  name: "updateReward",
  type: RewardType,
  args: {
    updateRewardInput: t.arg(t.NonNullInput(updateRewardInput)),
  },
  resolve: async (
    _,
    { updateRewardInput: { id, price, title } },
    { pool, auth },
  ) => {
    const patch: QReward.Updatable = {
      ...(title === null || title === undefined ? undefined : { title }),
      ...(price === null || price === undefined ? undefined : { price }),
    }
    if (isObjectEmpty(patch)) return

    return (
      await db
        .update("Reward", patch, {
          id: Number(id),
          userId: auth.id,
        })
        .run(pool)
    ).at(0)
  },
})

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

    const newMaxEnergy = newLevel * 3
    const newMaxHitpoints = newLevel * 10

    const hitpointsReward = Math.max(1, rewardAmount / 2)
    const energyReward = Math.max(1, rewardAmount / 5)
    const goldReward = Math.max(1, rewardAmount)

    const damage = Math.round(Math.max(1, rewardAmount / 2))

    const battle = (
      await db
        .update(
          "Battle",
          { creatureHitpoints: dc.subtract(damage) },
          { partyLeaderId: Number(user.partyLeaderOrUserId) },
        )
        .run(txnClient)
    ).at(0)
    if (battle !== undefined) {
      if (battle.creatureHitpoints <= 0) {
        await battleVictory({ ...ctx, pool: txnClient }, battle, user)
      }
    }

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

async function battleVictory({ pool }: AppContext, battle: Battle, user: User) {
  await db
    .deletes("Battle", {
      partyLeaderId: Number(user.partyLeaderOrUserId),
    })
    .run(pool)

  const creature = await db
    .selectOne("Creature", { id: battle.creatureId })
    .run(pool)
  if (creature === undefined) return

  if (!user.firebaseToken) return

  const payload = {
    notification: {
      title: "Victory!",
      body: `You defeated a ${creature.name} ${creature.emoji} !`,
    },
  }

  user.firebaseToken && sendNotification(user.firebaseToken, payload)
}
