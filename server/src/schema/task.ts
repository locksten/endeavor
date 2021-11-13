import { AppContext } from "context"
import { db, dc } from "database"
import { ObjectType } from "gqtx"
import { DateType } from "schema/date"
import { giveRewardForTodo } from "schema/reward"
import { idResolver, t, typeResolver } from "schema/typesFactory"
import { isObjectEmpty } from "utils"
import { Task as QTask } from "zapatos/schema"

export { Task as QTask } from "zapatos/schema"
export type Task = QTask.JSONSelectable & { _type?: "Task" }

export const TaskType: ObjectType<AppContext, Task> = t.objectType<Task>({
  name: "Task",
  fields: () => [
    idResolver,
    typeResolver("Task"),
    t.field({
      name: "isCompleted",
      type: t.NonNull(t.Boolean),
      resolve: async ({ id }, _args, { pool }) => {
        return (
          (await db
            .selectOne("Task", { id, completionDate: dc.isNotNull })
            .run(pool)) !== undefined
        )
      },
    }),
    t.field({ name: "completionDate", type: DateType }),
    t.field({ name: "title", type: t.NonNull(t.String) }),
    t.field({ name: "difficulty", type: t.NonNull(t.Int) }),
    t.field({ name: "createdAt", type: t.NonNull(DateType) }),
  ],
})

export const createTaskInput = t.inputObjectType({
  name: "CreateTaskInput",
  fields: () => ({
    title: t.arg(t.NonNullInput(t.String)),
    difficulty: t.arg(t.NonNullInput(t.Int)),
  }),
})

export const mutationCreateTask = t.field({
  name: "createTask",
  type: TaskType,
  args: {
    createTaskInput: t.arg(t.NonNullInput(createTaskInput)),
  },
  resolve: async (_, { createTaskInput: input }, { pool, auth }) => {
    if (!auth.id) return

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

export const mutationDeleteTask = t.field({
  name: "deleteTask",
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
    return deletedIds.at(0) !== undefined ? String(deletedIds[0].id) : undefined
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
    ).at(0)

    if (task === undefined) return

    await giveRewardForTodo(ctx, task.difficulty)

    return task
  },
})

export const updateTaskInput = t.inputObjectType({
  name: "UpdateTaskInput",
  fields: () => ({
    id: t.arg(t.NonNullInput(t.ID)),
    title: t.arg(t.String),
    difficulty: t.arg(t.Int),
  }),
})

export const mutationUpdateTask = t.field({
  name: "updateTask",
  type: TaskType,
  args: {
    updateTaskInput: t.arg(t.NonNullInput(updateTaskInput)),
  },
  resolve: async (
    _,
    { updateTaskInput: { id, difficulty, title } },
    { pool, auth },
  ) => {
    const patch: QTask.Updatable = {
      ...(title === null || title === undefined ? undefined : { title }),
      ...(difficulty === null || difficulty === undefined
        ? undefined
        : { difficulty }),
    }
    if (isObjectEmpty(patch)) return

    return (
      await db
        .update("Task", patch, {
          id: Number(id),
          userId: auth.id,
        })
        .run(pool)
    ).at(0)
  },
})
