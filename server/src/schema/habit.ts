import { db, dc } from "database"
import { DateType } from "schema/date"
import { givePenaltyForTodo, giveRewardForTodo } from "schema/reward"
import { idResolver, t, typeResolver } from "schema/typesFactory"
import { isObjectEmpty } from "utils"
import { Habit as QHabit } from "zapatos/schema"

export { Habit as QHabit } from "zapatos/schema"
export type Habit = QHabit.JSONSelectable & { _type?: "Habit" }

export const HabitType = t.objectType<Habit>({
  name: "Habit",
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
    title: t.arg(t.NonNullInput(t.String)),
    difficulty: t.arg(t.NonNullInput(t.Int)),
    positiveCount: t.arg(t.NonNullInput(t.Boolean)),
    negativeCount: t.arg(t.NonNullInput(t.Boolean)),
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
    { createHabitInput: { title, difficulty, positiveCount, negativeCount } },
    { pool, auth },
  ) => {
    if (!auth.id) return

    const habit: QHabit.Insertable = {
      title,
      difficulty,
      positiveCount: positiveCount ? 0 : null,
      negativeCount: negativeCount ? 0 : null,
      userId: auth.id,
    }

    try {
      return await db.insert("Habit", habit).run(pool)
    } catch (e) {
      console.log(e)
    }
  },
})

export const mutationDeleteHabit = t.field({
  name: "deleteHabit",
  type: t.ID,
  args: {
    id: t.arg(t.NonNullInput(t.ID)),
  },
  resolve: async (_, { id }, { pool, auth }) => {
    const deletedIds = await db
      .deletes(
        "Habit",
        { userId: auth.id, id: Number(id) },
        { returning: ["id"] },
      )
      .run(pool)
    return deletedIds.at(0) !== undefined ? String(deletedIds[0].id) : undefined
  },
})

export const mutationDoNegativeHabit = t.field({
  name: "doNegativeHabit",
  type: HabitType,
  args: {
    id: t.arg(t.NonNullInput(t.ID)),
  },
  resolve: async (_, { id }, ctx) => {
    const { pool, auth } = ctx

    const habit = (
      await db
        .update(
          "Habit",
          { negativeCount: dc.subtract(1) },
          {
            id: Number(id),
            userId: auth.id,
            negativeCount: dc.isNotNull,
          },
        )
        .run(pool)
    ).at(0)

    if (habit === undefined) return

    await givePenaltyForTodo(ctx, habit.difficulty)

    return habit
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

    const habit = (
      await db
        .update(
          "Habit",
          { positiveCount: dc.add(1) },
          {
            id: Number(id),
            userId: auth.id,
            positiveCount: dc.isNotNull,
          },
        )
        .run(pool)
    ).at(0)

    if (habit === undefined) return

    await giveRewardForTodo(ctx, habit.difficulty)

    return habit
  },
})

export const updateHabitInput = t.inputObjectType({
  name: "UpdateHabitInput",
  fields: () => ({
    id: t.arg(t.NonNullInput(t.ID)),
    title: t.arg(t.String),
    difficulty: t.arg(t.Int),
    positiveCount: t.arg(t.Boolean),
    negativeCount: t.arg(t.Boolean),
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
    {
      updateHabitInput: { id, positiveCount, negativeCount, difficulty, title },
    },
    { pool, auth },
  ) => {
    const patch: QHabit.Updatable = {
      ...(title === null || title === undefined ? undefined : { title }),
      ...(difficulty === null || difficulty === undefined
        ? undefined
        : { difficulty }),
      ...(positiveCount === undefined
        ? undefined
        : { positiveCount: positiveCount ? 0 : null }),
      ...(negativeCount === undefined
        ? undefined
        : { positiveCount: negativeCount ? 0 : null }),
    }
    if (isObjectEmpty(patch)) return

    return (
      await db
        .update("Habit", patch, {
          id: Number(id),
          userId: auth.id,
        })
        .run(pool)
    ).at(0)
  },
})
