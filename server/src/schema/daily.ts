import { AppContext } from "context"
import { db, dc } from "database"
import { ObjectType } from "gqtx"
import { DateType } from "schema/date"
import { giveRewardForTodo } from "schema/todoConsequences"
import {
  createTodo,
  CreateTodoInput,
  dcTodoIdBelongsToUser,
  Todo,
  TodoInterface,
  updateTodo,
  UpdateTodoInput,
} from "schema/todo"
import { idResolver, t, typeResolver } from "schema/typesFactory"
import { getLastMidnight } from "utils"
import { Daily as QDaily } from "zapatos/schema"

export { Daily as QDaily, TodoDaily as QTodoDaily } from "zapatos/schema"
export type Daily = QDaily.JSONSelectable

export type TodoDaily = Todo & { type: "Daily" } & Daily

export const DailyType: ObjectType<AppContext, TodoDaily> =
  t.objectType<TodoDaily>({
    name: "Daily",
    interfaces: [TodoInterface],
    isTypeOf: (thing: Todo) => thing.type === "Daily",
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
    createTodoInput: t.arg(t.NonNullInput(CreateTodoInput)),
  }),
})

export const mutationCreateDaily = t.field({
  name: "createDaily",
  type: DailyType,
  args: {
    createDailyInput: t.arg(t.NonNullInput(createDailyInput)),
  },
  resolve: async (_, { createDailyInput: { createTodoInput } }, ctx) => {
    const { pool, auth } = ctx
    if (!auth.id) return

    const id = await createTodo(ctx, "Daily", {}, createTodoInput)
    if (id === undefined) return

    return await db.selectOne("TodoDaily", { id }).run(pool)
  },
})

export const updateDailyInput = t.inputObjectType({
  name: "UpdateDailyInput",
  fields: () => ({
    id: t.arg(t.NonNullInput(t.ID)),
    updateTodoInput: t.arg(t.NonNullInput(UpdateTodoInput)),
  }),
})

export const mutationUpdateDaily = t.field({
  name: "updateDaily",
  type: DailyType,
  args: {
    updateDailyInput: t.arg(t.NonNullInput(updateDailyInput)),
  },
  resolve: async (_, { updateDailyInput: { id, updateTodoInput } }, ctx) => {
    const { auth, pool } = ctx
    if (!auth.id) return

    const todo = await updateTodo(ctx, Number(id), updateTodoInput)
    if (todo?.at(0) === undefined) return

    return await db.selectOne("TodoDaily", { id: Number(id) }).run(pool)
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
    if (!auth.id) return

    const daily = (
      await db
        .update(
          "Daily",
          { lastCompletionDate: new Date() },
          {
            id: dcTodoIdBelongsToUser(Number(id), auth.id),
            lastCompletionDate: dc.or(dc.isNull, dc.lt(getLastMidnight())),
          },
        )
        .run(pool)
    ).at(0)

    if (daily === undefined) return

    const todoDaily = (await db
      .selectOne("TodoDaily", { id: Number(id) })
      .run(pool)) as TodoDaily | undefined
    if (todoDaily === undefined) return

    await giveRewardForTodo(ctx, todoDaily.difficulty)
    return todoDaily
  },
})
