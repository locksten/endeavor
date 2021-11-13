import { db, dc } from "database"
import { DailyType } from "schema/daily"
import { TaskType } from "schema/task"
import { idResolver, t } from "schema/typesFactory"
import { UserType } from "schema/user"
import { getLastMidnight } from "utils"

export type Me = { id: number }

export const MeType = t.objectType<Me>({
  name: "Me",
  fields: () => [
    idResolver,
    t.field("user", {
      type: t.NonNull(UserType),
      resolve: async (me, _args, { pool }) => {
        return await db.selectOne("User", { id: me.id }).run(pool)
      },
    }),
    t.field("dailies", {
      type: t.NonNull(t.List(t.NonNull(DailyType))),
      resolve: async (me, _args, { pool }) => {
        return await db.select("Daily", { userId: me.id }).run(pool)
      },
    }),
    t.field("tasks", {
      type: t.NonNull(t.List(t.NonNull(TaskType))),
      resolve: async (me, _args, { pool }) => {
        return await db
          .select("Task", {
            userId: me.id,
            completionDate: dc.or(dc.isNull, dc.gt(getLastMidnight())),
          })
          .run(pool)
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
