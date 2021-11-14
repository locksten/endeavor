import { AppContext } from "context"
import { db, dc } from "database"
import { ObjectType } from "gqtx"
import { DailyType } from "schema/daily"
import { HabitType } from "schema/habit"
import { QInvite } from "schema/invite"
import { TaskType } from "schema/task"
import { idResolver, t } from "schema/typesFactory"
import { QUser, User, UserType } from "schema/user"
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
    t.field({
      name: "partyLeader",
      type: UserType,
      resolve: async (me, _args, { pool }) => {
        return (
          await db.sql<QUser.SQL, User[]>`
            SELECT ${"User"}.*
            FROM ${"User"}
            WHERE ${"id"} =
            (SELECT ${"partyLeaderId"}
             FROM ${"User"}
             WHERE ${"User"}.${"id"} = ${db.param(me.id)})
            `.run(pool)
        ).at(0)
      },
    }),
    t.field({
      name: "partyMembers",
      type: t.NonNull(t.List(t.NonNull(UserType))),
      resolve: async (me, _args, { pool }) => {
        return await db.sql<QUser.SQL, User[]>`
            SELECT ${"User"}.*
            FROM ${"User"}
            WHERE ${"partyLeaderId"} IS NOT NULL
            AND ${"partyLeaderId"} =
            (SELECT ${"partyLeaderId"}
             FROM ${"User"}
             WHERE ${"User"}.${"id"} = ${db.param(me.id)})
            `.run(pool)
      },
    }),
    t.field({
      name: "inviters",
      type: t.NonNull(t.List(t.NonNull(UserType))),
      resolve: async (me, _args, { pool }) => {
        return await db.sql<QInvite.SQL | QUser.SQL, User[]>`
            SELECT ${"User"}.*
            FROM ${"Invite"}
            JOIN ${"User"} ON ${"Invite"}.${"inviterId"} = ${"User"}.${"id"}
            WHERE ${"inviteeId"} = ${db.param(me.id)}`.run(pool)
      },
    }),
    t.field({
      name: "invitees",
      type: t.NonNull(t.List(t.NonNull(UserType))),
      resolve: async (me, _args, { pool }) => {
        return await db.sql<QInvite.SQL | QUser.SQL, User[]>`
            SELECT ${"User"}.*
            FROM ${"Invite"}
            JOIN ${"User"} ON ${"Invite"}.${"inviteeId"} = ${"User"}.${"id"}
            WHERE ${"inviterId"} = ${db.param(me.id)}`.run(pool)
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
