import { db } from "database"
import { Interface } from "gqtx"
import { DateType } from "schema/date"
import { t } from "schema/typesFactory"
import { AppContext } from "context"
import { Nullable, isObjectEmpty } from "utils"

import { Todo as QTodo } from "zapatos/schema"
import { QHabit } from "schema/habit"
import { QDaily } from "schema/daily"
import { QTask } from "schema/task"
export { Todo as QTodo } from "zapatos/schema"
export type Todo = QTodo.JSONSelectable

export const TodoInterface: Interface<AppContext, Todo | null> =
  t.interfaceType<Todo>({
    name: "Todo",
    fields: () => [
      t.abstractField({ name: "id", type: t.NonNull(t.ID) }),
      t.abstractField({ name: "title", type: t.NonNull(t.String) }),
      t.abstractField({ name: "difficulty", type: t.NonNull(t.Int) }),
      t.abstractField({ name: "createdAt", type: t.NonNull(DateType) }),
    ],
  })

export const CreateTodoInput = t.inputObjectType({
  name: "CreateTodoInput",
  fields: () => ({
    title: t.arg(t.NonNullInput(t.String)),
    difficulty: t.arg(t.NonNullInput(t.Int)),
  }),
})

export const createTodo = async (
  { pool, auth }: AppContext,
  type: QHabit.Table | QDaily.Table | QTask.Table,
  typeValues: Omit<
    QHabit.Insertable | QDaily.Insertable | QTask.Insertable,
    "id"
  >,
  createTodoInput: Omit<QTodo.Insertable, "id" | "type" | "userId">,
) => {
  if (!auth.id) return

  const todo = {
    ...createTodoInput,
    type,
    userId: auth.id,
  }

  const comma = db.raw(isObjectEmpty(typeValues) ? "" : ",")

  return (
    await db.sql<
      QTodo.SQL | QHabit.SQL | QDaily.SQL | QTask.SQL,
      { id: number }[]
    >`
      WITH todo AS (
        INSERT INTO ${"Todo"}(${db.cols(todo)})
        VALUES(${db.vals(todo)})
        RETURNING id
      )
      INSERT INTO ${type}(${"id"}${comma} ${db.cols(typeValues)})
      VALUES((SELECT id FROM todo)${comma} ${db.vals(typeValues)})
      RETURNING id
    `.run(pool)
  ).at(0)?.id
}

export const UpdateTodoInput = t.inputObjectType({
  name: "UpdateTodoInput",
  fields: () => ({
    title: t.arg(t.String),
    difficulty: t.arg(t.Int),
  }),
})

export const updateTodo = async (
  { pool, auth }: AppContext,
  id: number,
  { title, difficulty }: Nullable<QTodo.Updatable>,
) => {
  const patch: QTodo.Updatable = {
    ...(title === null || title === undefined ? undefined : { title }),
    ...(difficulty === null || difficulty === undefined
      ? undefined
      : { difficulty }),
  }
  if (isObjectEmpty(patch)) return

  return await db
    .update("Todo", patch, {
      id,
      userId: auth.id,
    })
    .run(pool)
}

export const mutationDeleteTodo = t.field({
  name: "deleteTodo",
  type: t.ID,
  args: {
    id: t.arg(t.NonNullInput(t.ID)),
  },
  resolve: async (_, { id }, { pool, auth }) => {
    const deleted = (
      await db
        .deletes(
          "Todo",
          { userId: auth.id, id: Number(id) },
          { returning: ["id"] },
        )
        .run(pool)
    ).at(0)
    return deleted && String(deleted.id)
  },
})

export const dcTodoIdBelongsToUser = (id: number, userId: number) =>
  db.sql<QTodo.SQL>`${
    db.self
  } = (SELECT ${"id"} FROM ${"Todo"} WHERE ${"Todo"}.${"id"} = ${db.param(
    id,
  )} AND ${"Todo"}.${"userId"} = ${db.param(userId)})`
