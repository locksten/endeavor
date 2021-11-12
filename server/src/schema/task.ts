import { AppContext } from "context"
import { db } from "database"
import { ObjectType } from "gqtx/dist/types"
import { DateType } from "schema/date"
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
          (await db.selectExactlyOne("Task", { id }).run(pool))
            ?.completionDate !== null
        )
      },
    }),
    t.defaultField("completionDate", DateType),
    t.defaultField("title", t.NonNull(t.String)),
    t.defaultField("difficulty", t.NonNull(t.Int)),
    t.defaultField("createdAt", t.NonNull(DateType)),
  ],
})

export const mutationCreateTask = t.field("createTask", {
  type: TaskType,
  args: {
    title: t.arg(t.NonNullInput(t.String)),
    difficulty: t.arg(t.NonNullInput(t.Int)),
  },
  resolve: async (_, { title, difficulty }, { pool, auth }) => {
    if (!auth.id) return undefined

    const task: Omit<Task, "id" | "createdAt" | "completionDate"> = {
      userId: auth.id,
      title,
      difficulty,
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
  resolve: async (_, { id }, { pool, auth }) => {
    const task = (
      await db
        .update(
          "Task",
          { completionDate: new Date() },
          {
            id: Number(id),
            userId: auth.id,
            completionDate: db.conditions.isNull,
          },
        )
        .run(pool)
    )?.at(0)

    if (task === undefined) return undefined

    await db
      .update(
        "User",
        {
          energy: db.sql`LEAST(${"maxEnergy"}, ${db.self} + ${db.param(
            task.difficulty,
          )})`,
          hitpoints: db.sql`LEAST(${"maxHitpoints"}, ${db.self} + ${db.param(
            task.difficulty,
          )})`,
          experience: db.sql`LEAST(1000, ${db.self} + ${db.param(
            task.difficulty,
          )})`,
        },
        { id: auth.id },
      )
      .run(pool)

    return task
  },
})

export const mutationUpdateTask = t.field("updateTask", {
  type: TaskType,
  args: {
    id: t.arg(t.NonNullInput(t.ID)),
    title: t.arg(t.NonNullInput(t.String)),
  },
  resolve: async (_, { id, title }, { pool, auth }) => {
    return (
      await db
        .update("Task", { title }, { id: Number(id), userId: auth.id })
        .run(pool)
    )?.at(0)
  },
})
