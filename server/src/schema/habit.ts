import { AppContext } from "context"
import { db, dc } from "database"
import { ObjectType } from "gqtx"
import { DateType } from "schema/date"
import { givePenaltyForTodo, giveRewardForTodo } from "schema/reward"
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
import { isObjectEmpty, Nullable } from "utils"
import { Habit as QHabit } from "zapatos/schema"

export { Habit as QHabit, TodoHabit as QTodoHabit } from "zapatos/schema"
export type Habit = QHabit.JSONSelectable

export type TodoHabit = Todo & { type: "Habit" } & Habit

export const HabitType: ObjectType<AppContext, TodoHabit> =
  t.objectType<TodoHabit>({
    name: "Habit",
    interfaces: [TodoInterface],
    isTypeOf: (thing: Todo) => thing.type === "Habit",
    fields: () => [
      idResolver,
      typeResolver("Habit"),
      t.field({ name: "title", type: t.NonNull(t.String) }),
      t.field({ name: "difficulty", type: t.NonNull(t.Int) }),
      t.field({ name: "positiveCount", type: t.Int }),
      t.field({ name: "negativeCount", type: t.Int }),
      t.field({ name: "createdAt", type: t.NonNull(DateType) }),
    ],
  })

export const createHabitInput = t.inputObjectType({
  name: "CreateHabitInput",
  fields: () => ({
    positiveCount: t.arg(t.NonNullInput(t.Boolean)),
    negativeCount: t.arg(t.NonNullInput(t.Boolean)),
    createTodoInput: t.arg(t.NonNullInput(CreateTodoInput)),
  }),
})

export const mutationCreateHabit = t.field({
  name: "createHabit",
  type: HabitType,
  args: {
    createHabitInput: t.arg(t.NonNullInput(createHabitInput)),
  },
  resolve: async (
    _,
    { createHabitInput: { createTodoInput, positiveCount, negativeCount } },
    ctx,
  ) => {
    const { pool, auth } = ctx
    if (!auth.id) return

    const habit: Omit<QHabit.Insertable, "id"> = {
      positiveCount: positiveCount ? 0 : null,
      negativeCount: negativeCount ? 0 : null,
    }

    const id = await createTodo(ctx, "Habit", habit, createTodoInput)
    if (id === undefined) return

    return await db.selectOne("TodoHabit", { id }).run(pool)
  },
})

export const updateHabitInput = t.inputObjectType({
  name: "UpdateHabitInput",
  fields: () => ({
    id: t.arg(t.NonNullInput(t.ID)),
    positiveCount: t.arg(t.Boolean),
    negativeCount: t.arg(t.Boolean),
    updateTodoInput: t.arg(t.NonNullInput(UpdateTodoInput)),
  }),
})

export const mutationUpdateHabit = t.field({
  name: "updateHabit",
  type: HabitType,
  args: {
    updateHabitInput: t.arg(t.NonNullInput(updateHabitInput)),
  },
  resolve: async (
    _,
    { updateHabitInput: { id, positiveCount, negativeCount, updateTodoInput } },
    ctx,
  ) => {
    const { auth, pool } = ctx
    if (!auth.id) return

    const [todo, _habit] = await db.serializable(pool, (txnClient) =>
      Promise.all([
        updateTodo({ ...ctx, pool: txnClient }, Number(id), updateTodoInput),
        updateHabit({ ...ctx, pool: txnClient }, Number(id), {
          positiveCount,
          negativeCount,
        }),
      ]),
    )
    if (todo?.at(0) === undefined) return

    return await db.selectOne("TodoHabit", { id: Number(id) }).run(pool)
  },
})

export const updateHabit = async (
  { pool, auth }: AppContext,
  id: number,
  {
    positiveCount,
    negativeCount,
  }: Partial<Nullable<{ positiveCount: boolean; negativeCount: boolean }>>,
) => {
  if (!auth.id) return

  const patch: QHabit.Updatable = {
    ...(positiveCount === undefined
      ? undefined
      : { positiveCount: positiveCount ? 0 : null }),
    ...(negativeCount === undefined
      ? undefined
      : { negativeCount: negativeCount ? 0 : null }),
  }
  if (isObjectEmpty(patch)) return

  return await db
    .update("Habit", patch, {
      id: dcTodoIdBelongsToUser(Number(id), auth.id),
    })
    .run(pool)
}

export const mutationDoNegativeHabit = t.field({
  name: "doNegativeHabit",
  type: HabitType,
  args: {
    id: t.arg(t.NonNullInput(t.ID)),
  },
  resolve: async (_, { id }, ctx) => {
    const { pool, auth } = ctx
    if (!auth.id) return

    const habit = (
      await db
        .update(
          "Habit",
          { negativeCount: dc.add(1) },
          {
            id: dcTodoIdBelongsToUser(Number(id), auth.id),
            negativeCount: dc.isNotNull,
          },
        )
        .run(pool)
    ).at(0)

    if (habit === undefined) return

    const todoHabit = (await db
      .selectOne("TodoHabit", { id: Number(id) })
      .run(pool)) as TodoHabit | undefined
    if (todoHabit === undefined) return

    await givePenaltyForTodo(ctx, todoHabit.difficulty)
    return todoHabit
  },
})

export const mutationDoPositiveHabit = t.field({
  name: "doPositiveHabit",
  type: HabitType,
  args: {
    id: t.arg(t.NonNullInput(t.ID)),
  },
  resolve: async (_, { id }, ctx) => {
    const { pool, auth } = ctx
    if (!auth.id) return

    const habit = (
      await db
        .update(
          "Habit",
          { positiveCount: dc.add(1) },
          {
            id: dcTodoIdBelongsToUser(Number(id), auth.id),
            positiveCount: dc.isNotNull,
          },
        )
        .run(pool)
    ).at(0)

    if (habit === undefined) return

    const todoHabit = (await db
      .selectOne("TodoHabit", { id: Number(id) })
      .run(pool)) as TodoHabit | undefined
    if (todoHabit === undefined) return

    await giveRewardForTodo(ctx, todoHabit.difficulty)
    return todoHabit
  },
})
