import { db } from "database"
import { NoFragmentCyclesRule } from "graphql"
import { QTask, Task, TaskType } from "schema/task"
import { QTodo } from "schema/todo"
import { idResolver, t } from "schema/typesFactory"
import { UserType } from "schema/user"

export type Me = { id: number }

export const MeType = t.objectType<Me>({
  name: "Me",
  fields: () => [
    idResolver,
    t.field("user", {
      type: t.NonNull(UserType),
      resolve: async (me, _args, { pool }) => {
        return await db.selectExactlyOne("User", { id: me.id }).run(pool)
      },
    }),
    t.field("tasks", {
      type: t.NonNull(t.List(t.NonNull(TaskType))),
      resolve: async (me, _args, { pool }) => {
        return await db.sql<QTask.SQL | QTodo.SQL, Task[]>`
        SELECT ${"Todo"}.*, ${"Task"}.*
        FROM ${"Todo"}
        JOIN ${"Task"} ON ${"Todo"}.${"id"} = ${"Task"}.${"id"}
        WHERE ${{ userId: me.id }}
        `.run(pool)
      },
    }),
  ],
})

export const queryMe = t.field("me", {
  type: MeType,
  resolve: (_, _args, { auth }) => {
    return auth.id ? { id: auth.id } : null
  },
})
