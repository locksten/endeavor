import { AppContext } from "context"
import { db, dc } from "database"
import { ObjectType } from "gqtx/dist/types"
import { DateType } from "schema/date"
import { giveRewardForTodo } from "schema/reward"
import { idResolver, t, typeResolver } from "schema/typesFactory"
import { getLastMidnight } from "utils"
import { Daily as QDaily } from "zapatos/schema"

export { Daily as QDaily } from "zapatos/schema"
export type Daily = QDaily.JSONSelectable & { _type?: "Daily" }

export const DailyType: ObjectType<AppContext, Daily> = t.objectType<Daily>({
  name: "Daily",
  fields: () => [
    idResolver,
    typeResolver("Daily"),
    t.field("isCompleted", {
      type: t.NonNull(t.Boolean),
      resolve: async ({ id }, _args, { pool }) => {
        return (
          (await db
            .selectOne("Daily", {
              id,
              lastCompletionDate: dc.gte(getLastMidnight()),
            })
            .run(pool)) !== undefined
        )
      },
    }),
    t.defaultField("lastCompletionDate", DateType),
    t.defaultField("title", t.NonNull(t.String)),
    t.defaultField("difficulty", t.NonNull(t.Int)),
    t.defaultField("createdAt", t.NonNull(DateType)),
  ],
})

export const createDailyInput = t.inputObjectType({
  name: "CreateDailyInput",
  fields: () => ({
    title: { type: t.NonNullInput(t.String) },
    difficulty: { type: t.NonNullInput(t.Int) },
  }),
})

export const mutationCreateDaily = t.field("createDaily", {
  type: DailyType,
  args: {
    createDailyInput: t.arg(t.NonNullInput(createDailyInput)),
  },
  resolve: async (_, { createDailyInput: input }, { pool, auth }) => {
    if (!auth.id) return undefined

    const daily: Omit<Daily, "id" | "createdAt" | "lastCompletionDate"> = {
      userId: auth.id,
      ...input,
    }

    try {
      return await db.insert("Daily", daily).run(pool)
    } catch (e) {
      console.log(e)
    }
  },
})

export const mutationDeleteDaily = t.field("deleteDaily", {
  type: t.ID,
  args: {
    id: t.arg(t.NonNullInput(t.ID)),
  },
  resolve: async (_, { id }, { pool, auth }) => {
    const deletedIds = await db
      .deletes(
        "Daily",
        { userId: auth.id, id: Number(id) },
        { returning: ["id"] },
      )
      .run(pool)
    return deletedIds.length ? String(deletedIds[0].id) : undefined
  },
})

export const mutationCompleteDaily = t.field("completeDaily", {
  type: DailyType,
  args: {
    id: t.arg(t.NonNullInput(t.ID)),
  },
  resolve: async (_, { id }, ctx) => {
    const { pool, auth } = ctx

    const daily = (
      await db
        .update(
          "Daily",
          { lastCompletionDate: new Date() },
          {
            id: Number(id),
            userId: auth.id,
            lastCompletionDate: dc.or(dc.isNull, dc.lt(getLastMidnight())),
          },
        )
        .run(pool)
    )?.at(0)

    if (daily === undefined) return undefined

    await giveRewardForTodo(ctx, daily.difficulty)

    return daily
  },
})

export const updateDailyInput = t.inputObjectType({
  name: "UpdateDailyInput",
  fields: () => ({
    id: { type: t.NonNullInput(t.ID) },
    title: { type: t.String },
    difficulty: { type: t.Int },
  }),
})

export const mutationUpdateDaily = t.field("updateDaily", {
  type: DailyType,
  args: {
    updateDailyInput: t.arg(t.NonNullInput(updateDailyInput)),
  },
  resolve: async (
    _,
    { updateDailyInput: { id, ...patch } },
    { pool, auth },
  ) => {
    return (
      await db
        .update("Daily", patch as Partial<Daily>, {
          id: Number(id),
          userId: auth.id,
        })
        .run(pool)
    )?.at(0)
  },
})
