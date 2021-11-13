import { AppContext } from "context"
import { db, dc } from "database"
import { ObjectType } from "gqtx"
import { DateType } from "schema/date"
import { giveRewardForTodo } from "schema/reward"
import { idResolver, t, typeResolver } from "schema/typesFactory"
import { getLastMidnight, isObjectEmpty } from "utils"
import { Daily as QDaily } from "zapatos/schema"

export { Daily as QDaily } from "zapatos/schema"
export type Daily = QDaily.JSONSelectable & { _type?: "Daily" }

export const DailyType: ObjectType<AppContext, Daily> = t.objectType<Daily>({
  name: "Daily",
  fields: () => [
    idResolver,
    typeResolver("Daily"),
    t.field({
      name: "isCompleted",
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
    t.field({ name: "lastCompletionDate", type: DateType }),
    t.field({ name: "title", type: t.NonNull(t.String) }),
    t.field({ name: "difficulty", type: t.NonNull(t.Int) }),
    t.field({ name: "createdAt", type: t.NonNull(DateType) }),
  ],
})

export const createDailyInput = t.inputObjectType({
  name: "CreateDailyInput",
  fields: () => ({
    title: t.arg(t.NonNullInput(t.String)),
    difficulty: t.arg(t.NonNullInput(t.Int)),
  }),
})

export const mutationCreateDaily = t.field({
  name: "createDaily",
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

export const mutationDeleteDaily = t.field({
  name: "deleteDaily",
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

    return deletedIds.at(0) !== undefined ? String(deletedIds[0].id) : undefined
  },
})

export const mutationCompleteDaily = t.field({
  name: "completeDaily",
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
    ).at(0)

    if (daily === undefined) return undefined

    await giveRewardForTodo(ctx, daily.difficulty)

    return daily
  },
})

export const updateDailyInput = t.inputObjectType({
  name: "UpdateDailyInput",
  fields: () => ({
    id: t.arg(t.NonNullInput(t.ID)),
    title: t.arg(t.String),
    difficulty: t.arg(t.Int),
  }),
})

export const mutationUpdateDaily = t.field({
  name: "updateDaily",
  type: DailyType,
  args: {
    updateDailyInput: t.arg(t.NonNullInput(updateDailyInput)),
  },
  resolve: async (
    _,
    { updateDailyInput: { id, title, difficulty } },
    { pool, auth },
  ) => {
    const patch: QDaily.Updatable = {
      ...(title === null || title === undefined ? undefined : { title }),
      ...(difficulty === null || difficulty === undefined
        ? undefined
        : { difficulty }),
    }
    if (isObjectEmpty(patch)) return
    return (
      await db
        .update("Daily", patch, {
          id: Number(id),
          userId: auth.id,
        })
        .run(pool)
    ).at(0)
  },
})
