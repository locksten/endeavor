import { AppContext } from "context"
import { db, dc } from "database"
import { ObjectType } from "gqtx"
import { DateType } from "schema/date"
import { giveRewardForTodo } from "schema/reward"
import {
  createTodo,
  CreateTodoInput,
  dcTodoIdBelongsToUser,
  QTodo,
  Todo,
  TodoInterface,
  updateTodo,
  UpdateTodoInput,
} from "schema/todo"
import { idResolver, t, typeResolver } from "schema/typesFactory"

import { Task as QTask } from "zapatos/schema"
export { Task as QTask } from "zapatos/schema"
export type Task = QTask.JSONSelectable

export { TodoTask as QTodoTask } from "zapatos/schema"
export type TodoTask = Todo & { type: "Task" } & Task

export const TaskType: ObjectType<AppContext, TodoTask> =
  t.objectType<TodoTask>({
    name: "Task",
    interfaces: [TodoInterface],
    isTypeOf: (thing: Todo) => thing.type === "Task",
    fields: () => [
      typeResolver("Task"),
      idResolver,
      t.field({
        name: "isCompleted",
        type: t.NonNull(t.Boolean),
        resolve: async ({ id }, _args, { pool }) => {
          return (
            (await db
              .selectOne("TodoTask", { id, completionDate: dc.isNotNull })
              .run(pool)) !== undefined
          )
        },
      }),
      t.field({ name: "title", type: t.NonNull(t.String) }),
      t.field({ name: "difficulty", type: t.NonNull(t.Int) }),
      t.field({ name: "completionDate", type: DateType }),
      t.field({ name: "createdAt", type: t.NonNull(DateType) }),
    ],
  })

export const createTaskInput = t.inputObjectType({
  name: "CreateTaskInput",
  fields: () => ({
    createTodoInput: t.arg(t.NonNullInput(CreateTodoInput)),
  }),
})

export const mutationCreateTask = t.field({
  name: "createTask",
  type: TaskType,
  args: {
    createTaskInput: t.arg(t.NonNullInput(createTaskInput)),
  },
  resolve: async (_, { createTaskInput: { createTodoInput } }, ctx) => {
    const { pool, auth } = ctx
    if (!auth.id) return

    const id = await createTodo(ctx, "Task", {}, createTodoInput)
    if (id === undefined) return

    return await db.selectOne("TodoTask", { id }).run(pool)
  },
})

export const updateTaskInput = t.inputObjectType({
  name: "UpdateTaskInput",
  fields: () => ({
    id: t.arg(t.NonNullInput(t.ID)),
    updateTodoInput: t.arg(t.NonNullInput(UpdateTodoInput)),
  }),
})

export const mutationUpdateTask = t.field({
  name: "updateTask",
  type: TaskType,
  args: {
    updateTaskInput: t.arg(t.NonNullInput(updateTaskInput)),
  },
  resolve: async (_, { updateTaskInput: { id, updateTodoInput } }, ctx) => {
    const { auth, pool } = ctx
    if (!auth.id) return

    const todo = await updateTodo(ctx, Number(id), updateTodoInput)
    if (todo?.at(0) === undefined) return

    return await db.selectOne("TodoTask", { id: Number(id) }).run(pool)
  },
})

export const mutationCompleteTask = t.field({
  name: "completeTask",
  type: TaskType,
  args: {
    id: t.arg(t.NonNullInput(t.ID)),
  },
  resolve: async (_, { id }, ctx) => {
    const { pool, auth } = ctx
    if (!auth.id) return

    const task = (
      await db
        .update(
          "Task",
          { completionDate: new Date() },
          {
            id: dcTodoIdBelongsToUser(Number(id), auth.id),
            completionDate: dc.isNull,
          },
        )
        .run(pool)
    ).at(0)
    if (task === undefined) return

    const todoTask = (await db
      .selectOne("TodoTask", { id: Number(id) })
      .run(pool)) as TodoTask | undefined
    if (todoTask === undefined) return

    await giveRewardForTodo(ctx, todoTask.difficulty)
    return todoTask
  },
})
