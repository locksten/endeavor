import { AppContext } from "context"
import { db, dc } from "database"
import { ObjectType } from "gqtx/dist/types"
import { DateType } from "schema/date"
import { giveRewardForTodo } from "schema/reward"
import { idResolver, t, typeResolver } from "schema/typesFactory"
import { Task as QTask } from "zapatos/schema"

export { Task as QTask } from "zapatos/schema"
export type Task = QTask.JSONSelectable & { _type?: "Task" }

export const TaskType: ObjectType<AppContext, Task> = t.objectType<Task>({
  name: "Task",
  fields: () => [
    idResolver,
    typeResolver("Task"),
    t.field("isCompleted", {
      type: t.NonNull(t.Boolean),
      resolve: async ({ id }, _args, { pool }) => {
        return (
          (await db
            .selectOne("Task", { id, completionDate: dc.isNotNull })
            .run(pool)) !== undefined
        )
      },
    }),
    t.defaultField("completionDate", DateType),
    t.defaultField("title", t.NonNull(t.String)),
    t.defaultField("difficulty", t.NonNull(t.Int)),
    t.defaultField("createdAt", t.NonNull(DateType)),
  ],
})

export const createTaskInput = t.inputObjectType({
  name: "CreateTaskInput",
  fields: () => ({
    title: { type: t.NonNullInput(t.String) },
    difficulty: { type: t.NonNullInput(t.Int) },
  }),
})

export const mutationCreateTask = t.field("createTask", {
  type: TaskType,
  args: {
    createTaskInput: t.arg(t.NonNullInput(createTaskInput)),
  },
  resolve: async (_, { createTaskInput: input }, { pool, auth }) => {
    if (!auth.id) return undefined

    const task: Omit<Task, "id" | "createdAt" | "completionDate"> = {
      userId: auth.id,
      ...input,
    }

    try {
      return await db.insert("Task", task).run(pool)
    } catch (e) {
      console.log(e)
    }
  },
})

export const mutationDeleteTask = t.field("deleteTask", {
  type: t.ID,
  args: {
    id: t.arg(t.NonNullInput(t.ID)),
  },
  resolve: async (_, { id }, { pool, auth }) => {
    const deletedIds = await db
      .deletes(
        "Task",
        { userId: auth.id, id: Number(id) },
        { returning: ["id"] },
      )
      .run(pool)
    return deletedIds.length ? String(deletedIds[0].id) : undefined
  },
})

export const mutationCompleteTask = t.field("completeTask", {
  type: TaskType,
  args: {
    id: t.arg(t.NonNullInput(t.ID)),
  },
  resolve: async (_, { id }, ctx) => {
    const { pool, auth } = ctx

    const task = (
      await db
        .update(
          "Task",
          { completionDate: new Date() },
          {
            id: Number(id),
            userId: auth.id,
            completionDate: dc.isNull,
          },
        )
        .run(pool)
    )?.at(0)

    if (task === undefined) return undefined

    await giveRewardForTodo(ctx, task.difficulty)

    return task
  },
})

export const updateTaskInput = t.inputObjectType({
  name: "UpdateTaskInput",
  fields: () => ({
    id: { type: t.NonNullInput(t.ID) },
    title: { type: t.String },
    difficulty: { type: t.Int },
  }),
})

export const mutationUpdateTask = t.field("updateTask", {
  type: TaskType,
  args: {
    updateTaskInput: t.arg(t.NonNullInput(updateTaskInput)),
  },
  resolve: async (_, { updateTaskInput: { id, ...input } }, { pool, auth }) => {
    return (
      await db
        .update("Task", input as Partial<Task>, {
          id: Number(id),
          userId: auth.id,
        })
        .run(pool)
    )?.at(0)
  },
})
