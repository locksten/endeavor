import { AppContext } from "context"
import { db, dc } from "database"
import { ObjectType } from "gqtx"
import { CreatureType, QCreature } from "schema/creature"
import { DateType } from "schema/date"
import { checkBattleVictory } from "schema/todoConsequences"
import { idResolver, t, _typeResolver } from "schema/typesFactory"
import { QUser, UserType } from "schema/user"
import { Battle as QBattle } from "zapatos/schema"

export { Battle as QBattle } from "zapatos/schema"
export type Battle = QBattle.JSONSelectable

export const BattleType: ObjectType<AppContext, Battle> = t.objectType<Battle>({
  name: "Battle",
  fields: () => [
    idResolver,
    _typeResolver("Battle"),
    t.field({
      name: "creature",
      type: CreatureType,
      resolve: async ({ creatureId }, _args, { pool }) => {
        return await db.selectOne("Creature", { id: creatureId }).run(pool)
      },
    }),
    t.field({ name: "createdAt", type: t.NonNull(DateType) }),
    t.field({ name: "creatureHitpoints", type: t.NonNull(t.Int) }),
    t.field({
      name: "partyMembers",
      type: t.NonNull(t.List(t.NonNull(UserType))),
      resolve: async ({ partyLeaderId }, _args, { pool }) => {
        return await db
          .select("User", { partyLeaderOrUserId: partyLeaderId })
          .run(pool)
      },
    }),
  ],
})

export const CreateBattleInput = t.inputObjectType({
  name: "CreateBattleInput",
  fields: () => ({
    creatureId: t.arg(t.NonNullInput(t.ID)),
  }),
})

export const mutationCreateBattle = t.field({
  name: "createBattle",
  type: BattleType,
  args: {
    createBattleInput: t.arg(t.NonNullInput(CreateBattleInput)),
  },
  resolve: async (_, { createBattleInput: { creatureId } }, { auth, pool }) => {
    if (!auth.id) return
    try {
      return await db
        .insert("Battle", {
          creatureId: Number(creatureId),
          creatureHitpoints: db.sql<QCreature.SQL>`
          (SELECT ${"maxHitpoints"} FROM ${"Creature"}
           WHERE ${"Creature"}.${"id"} = ${db.param(creatureId)})`,
          partyLeaderId: db.sql<QUser.SQL | QBattle.SQL>`
          (SELECT ${"partyLeaderOrUserId"} FROM ${"User"}
           WHERE ${"User"}.${"id"} = ${db.param(auth.id)})`,
        })
        .run(pool)
    } catch (e) {
      console.log(e)
    }
  },
})

export const mutationUseSpecialAttack = t.field({
  name: "useSpecialAttack",
  type: BattleType,
  args: {
    multiplier: t.arg(t.NonNullInput(t.Float)),
  },
  resolve: async (_, { multiplier }, ctx) => {
    const { auth, pool } = ctx
    if (!auth.id) return

    return await db.serializable(pool, async (txnClient) => {
      const user = await db.selectOne("User", { id: auth.id }).run(txnClient)
      if (user === undefined) return

      const energyCost = Math.floor(user.maxEnergy / 2)
      if (user.energy < energyCost) return
      const damage = Math.round(energyCost * multiplier)

      const newUser = (
        await db
          .update("User", { energy: dc.subtract(energyCost) }, { id: user.id })
          .run(txnClient)
      ).at(0)
      if (newUser === undefined) return

      const battle = (
        await db
          .update(
            "Battle",
            { creatureHitpoints: dc.subtract(damage) },
            { partyLeaderId: user.partyLeaderOrUserId },
          )
          .run(txnClient)
      ).at(0)
      if (battle === undefined) return

      await checkBattleVictory({ ...ctx, pool: txnClient }, newUser, battle)

      return battle
    })
  },
})

export const mutationUsePartyHeal = t.field({
  name: "usePartyHeal",
  type: BattleType,
  args: {},
  resolve: async (_, _args, ctx) => {
    const { auth, pool } = ctx
    if (!auth.id) return

    return await db.serializable(pool, async (txnClient) => {
      const user = await db.selectOne("User", { id: auth.id }).run(txnClient)
      if (user === undefined) return

      const energyCost = user.maxEnergy
      if (user.energy < energyCost) return
      const heal = energyCost

      const newUser = (
        await db
          .update("User", { energy: dc.subtract(energyCost) }, { id: user.id })
          .run(txnClient)
      ).at(0)
      if (newUser === undefined) return

      await db
        .update(
          "User",
          {
            hitpoints: db.sql<QUser.SQL>`LEAST(${db.self} + ${db.param(
              heal,
            )}, ${"maxHitpoints"})`,
          },
          { partyLeaderOrUserId: user.partyLeaderOrUserId },
        )
        .run(txnClient)

      const battle = (
        await db
          .select("Battle", { partyLeaderId: user.partyLeaderOrUserId })
          .run(txnClient)
      ).at(0)
      if (battle === undefined) return

      return battle
    })
  },
})
