import { AppContext } from "context"
import { db, dc } from "database"
import { ObjectType } from "gqtx"
import { DailyType } from "schema/daily"
import { HabitType } from "schema/habit"
import { TaskType } from "schema/task"
import { idResolver, t } from "schema/typesFactory"
import { User, UserType } from "schema/user"
import { getLastMidnight } from "utils"

export type Me = { id: number }

export const MeType: ObjectType<AppContext, User> = t.objectType<Me>({
  name: "Me",
  fields: () => [
    idResolver,
    t.field({
      name: "user",
      type: t.NonNull(UserType),
      resolve: async (me, _args, { pool }) => {
        return await db.selectOne("User", { id: me.id }).run(pool)
      },
    }),
    t.field({
      name: "habits",
      type: t.NonNull(t.List(t.NonNull(HabitType))),
      resolve: async (me, _args, { pool }) => {
        return await db.select("Habit", { userId: me.id }).run(pool)
      },
    }),
    t.field({
      name: "dailies",
      type: t.NonNull(t.List(t.NonNull(DailyType))),
      resolve: async (me, _args, { pool }) => {
        return await db.select("Daily", { userId: me.id }).run(pool)
      },
    }),
    t.field({
      name: "tasks",
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

export const queryMe = t.field({
  name: "me",
  type: MeType,
  resolve: (_, _args, { auth }) => {
    return auth.id ? { id: auth.id } : null
  },
})
