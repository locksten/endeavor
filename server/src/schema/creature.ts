import { AppContext } from "context"
import { db, dc } from "database"
import { ObjectType } from "gqtx"
import { DateType } from "schema/date"
import { idResolver, t, _typeResolver } from "schema/typesFactory"
import { isObjectEmpty } from "utils"
import { Creature as QCreature } from "zapatos/schema"

export { Creature as QCreature } from "zapatos/schema"
export type Creature = QCreature.JSONSelectable

export const CreatureType: ObjectType<AppContext, Creature> =
  t.objectType<Creature>({
    name: "Creature",
    fields: () => [
      idResolver,
      _typeResolver("Creature"),
      t.field({ name: "name", type: t.NonNull(t.String) }),
      t.field({ name: "emoji", type: t.NonNull(t.String) }),
      t.field({ name: "createdAt", type: t.NonNull(DateType) }),
      t.field({ name: "maxHitpoints", type: t.NonNull(t.Int) }),
      t.field({ name: "strength", type: t.NonNull(t.Int) }),
    ],
  })

export const queryCreatures = t.field({
  name: "creatures",
  type: t.NonNull(t.List(t.NonNull(CreatureType))),
  resolve: async (_, _args, { pool }) => {
    return await db.select("Creature", db.all).run(pool)
  },
})

export const CreateCreatureInput = t.inputObjectType({
  name: "CreateCreatureInput",
  fields: () => ({
    name: t.arg(t.NonNullInput(t.String)),
    emoji: t.arg(t.NonNullInput(t.String)),
    maxHitpoints: t.arg(t.NonNullInput(t.Int)),
    strength: t.arg(t.NonNullInput(t.Int)),
  }),
})

export const mutationCreateCreature = t.field({
  name: "createCreature",
  type: CreatureType,
  args: {
    createCreatureInput: t.arg(t.NonNullInput(CreateCreatureInput)),
  },
  resolve: async (_, { createCreatureInput }, { pool }) => {
    return await db.insert("Creature", createCreatureInput).run(pool)
  },
})
