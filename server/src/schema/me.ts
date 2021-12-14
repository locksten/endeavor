import { AppContext } from "context"
import { db, dc } from "database"
import { ObjectType } from "gqtx"
import { BattleType } from "schema/battle"
import { DailyType } from "schema/daily"
import { HabitType } from "schema/habit"
import { QInvite } from "schema/invite"
import { Item, ItemType, QItem } from "schema/item"
import { RewardType } from "schema/reward"
import { TaskType } from "schema/task"
import { idResolver, t } from "schema/typesFactory"
import { QUser, User, UserType } from "schema/user"
import { getLastMidnight } from "utils"
import { UserItem } from "zapatos/schema"

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
        return await db.select("TodoHabit", { userId: me.id }).run(pool)
      },
    }),
    t.field({
      name: "dailies",
      type: t.NonNull(t.List(t.NonNull(DailyType))),
      resolve: async (me, _args, { pool }) => {
        return await db.select("TodoDaily", { userId: me.id }).run(pool)
      },
    }),
    t.field({
      name: "tasks",
      type: t.NonNull(t.List(t.NonNull(TaskType))),
      resolve: async (me, _args, { pool }) => {
        return await db
          .select("TodoTask", {
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
            WHERE ${"partyLeaderOrUserId"} =
            (SELECT ${"partyLeaderOrUserId"}
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
    t.field({
      name: "totalEquipmentStats",
      type: t.NonNull(t.String),
      resolve: async (me, _args, { pool }) => {
        const user = await db.selectOne("User", { id: me.id }).run(pool)
        if (user === undefined) return

        let totalDefense = 0
        let totalStrength = 0

        if (user.offenseSlot !== null) {
          const item = await db
            .selectOne("Item", { id: user.offenseSlot })
            .run(pool)
          totalDefense += item?.defenseBonus ?? 0
          totalStrength += item?.strengthBonus ?? 0
        }
        if (user.defenseSlot !== null) {
          const item = await db
            .selectOne("Item", { id: user.defenseSlot })
            .run(pool)
          totalDefense += item?.defenseBonus ?? 0
          totalStrength += item?.strengthBonus ?? 0
        }
        if (user.accessorySlot !== null) {
          const item = await db
            .selectOne("Item", { id: user.accessorySlot })
            .run(pool)
          totalDefense += item?.defenseBonus ?? 0
          totalStrength += item?.strengthBonus ?? 0
        }
        return `🗡️ ${totalStrength}  🛡️ ${totalDefense}`
      },
    }),
    t.field({
      name: "inventory",
      type: t.NonNull(t.List(t.NonNull(ItemType))),
      resolve: async (me, _args, { pool }) => {
        return await db.sql<QItem.SQL | UserItem.SQL, Item[]>`
            SELECT ${"Item"}.*
            FROM ${"UserItem"}
            JOIN ${"Item"} ON ${"UserItem"}.${"itemId"} = ${"Item"}.${"id"}
            WHERE ${"userId"} = ${db.param(me.id)}`.run(pool)
      },
    }),
    t.field({
      name: "rewards",
      type: t.NonNull(t.List(t.NonNull(RewardType))),
      resolve: async (me, _args, { pool }) => {
        return await db.select("Reward", { userId: me.id }).run(pool)
      },
    }),
    t.field({
      name: "battle",
      type: BattleType,
      resolve: async (me, _args, { pool }) => {
        return await db
          .selectOne("Battle", {
            partyLeaderId: db.sql<QUser.SQL>`
              ${db.self} = (SELECT ${"partyLeaderOrUserId"} FROM ${"User"}
                            WHERE ${"User"}.${"id"} = ${db.param(me.id)})`,
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
