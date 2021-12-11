import { AppContext } from "context"
import { db } from "database"
import { ObjectType } from "gqtx"
import { CreatureType, QCreature } from "schema/creature"
import { DateType } from "schema/date"
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
