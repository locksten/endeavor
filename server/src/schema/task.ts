import { AppContext } from "context"
import { db } from "database"
import { ObjectType } from "gqtx/dist/types"
import { DateType } from "schema/date"
import { QTodo, Todo } from "schema/todo"
import { idResolver, t, typeResolver } from "schema/typesFactory"
import { Task as QTask } from "zapatos/schema"

export { Task as QTask } from "zapatos/schema"
export type Task = QTask.JSONSelectable & { _type?: "Task" }

export const TaskType: ObjectType<AppContext, (Task & Todo) | null> =
  t.objectType<Task & Todo>({
    name: "Task",
    fields: () => [
      idResolver,
      typeResolver("Task"),
      t.defaultField("isCompleted", t.NonNull(t.Boolean)),
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
    if (!auth.id) return null

    const todoArgs: Omit<Todo, "id" | "createdAt"> = {
      userId: auth.id,
      title,
      difficulty,
    }
    const taskArgs: Omit<Task, "id"> = { isCompleted: false }

    try {
      const [{ id }] = await db.sql<QTask.SQL | QTodo.SQL, [{ id: number }]>`
        WITH newTodo as (
          INSERT INTO ${"Todo"} (${db.cols(todoArgs)})
          VALUES (${db.vals(todoArgs)})
          RETURNING *
        )
        INSERT INTO ${"Task"} (${"id"}, ${db.cols(taskArgs)})
        VALUES (
          (SELECT ${"id"} FROM newTodo),
          ${db.vals(taskArgs)}
        )
        RETURNING ${"id"}
        `.run(pool)

      const [task] = await db.sql<QTask.SQL | QTodo.SQL, Task[]>`
        SELECT ${"Todo"}.*, ${"Task"}.*
        FROM ${"Todo"}
        JOIN ${"Task"} ON ${"Todo"}.${"id"} = ${"Task"}.${"id"}
        WHERE ${"Task"}.${"id"} = ${db.param(id)}
        `.run(pool)

      return task
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
        "Todo",
        { userId: auth.id, id: Number(id) },
        { returning: ["id"] },
      )
      .run(pool)
    return deletedIds.length ? String(deletedIds[0].id) : null
  },
})

export const mutationCompleteTask = t.field("completeTask", {
  type: TaskType,
  args: {
    id: t.arg(t.NonNullInput(t.ID)),
  },
  resolve: async (_, { id }, { pool, auth }) => {
    const updated = await db.sql<QTodo.SQL | QTask.SQL, [Todo & Task]>`
    UPDATE ${"Task"} AS atask
    SET ${"isCompleted"} = TRUE
    FROM ${"Todo"} AS atodo
    WHERE atask.${"id"} = atodo.${"id"}
    AND atodo.${"id"} = ${db.param(id)} 
    AND atodo.${"userId"} = ${db.param(auth.id)}
    RETURNING atask.*, atodo.*
    `.run(pool)
    return updated.length ? updated[0] : null
  },
})
