import { db, dc } from "database"
import { t } from "schema/typesFactory"
import { Invite as QInvite } from "zapatos/schema"

export { Invite as QInvite } from "zapatos/schema"
export type Invite = QInvite.JSONSelectable

export const mutationInviteToParty = t.field({
  name: "inviteToParty",
  type: t.ID,
  args: {
    username: t.arg(t.NonNullInput(t.String)),
  },
  resolve: async (_, { username: inviteeUsername }, { pool, auth }) => {
    try {
      // console.log()
      // console.log("INVITE")
      const invitee = await db
        .selectOne("User", {
          username: inviteeUsername,
        })
        .run(pool)
      // console.log("invitee:", invitee)
      if (invitee === undefined) return

      const invite = await db
        .insert("Invite", {
          inviterId: Number(auth.id),
          inviteeId: Number(invitee.id),
        })
        .run(pool)
      // console.log("invite:", invite)
      if (invite === undefined) return

      return String(invite)
    } catch (e) {
      console.log(e)
    }
  },
})

export const mutationAcceptInviteToParty = t.field({
  name: "acceptInviteToParty",
  type: t.ID,
  args: {
    id: t.arg(t.NonNullInput(t.ID)),
  },
  resolve: async (_, { id: inviterId }, { pool, auth }) => {
    try {
      console.log()
      console.log(
        `ACCEPT INVITE invitee(me) = ${auth.id}, inviter = ${inviterId}`,
      )

      const invite = (
        await db
          .deletes("Invite", {
            inviterId: Number(inviterId),
            inviteeId: Number(auth.id),
          })
          .run(pool)
      ).at(0)
      // console.log("delete invite:", invite)
      if (invite === undefined) return

      const inviterEnsure = await db
        .selectOne("User", {
          id: Number(inviterId),
          partyLeaderId: dc.or(dc.isNull, dc.eq(db.sql`${"id"}`)),
        })
        .run(pool)

      // console.log("ensure inviter is not in other party:", inviterEnsure)
      if (inviterEnsure === undefined) return

      const removed = await db
        .update(
          "User",
          { partyLeaderId: undefined },
          {
            partyLeaderId: Number(auth.id),
          },
        )
        .run(pool)
      // console.log("remove my members:", removed)

      const invitersLeader = await db
        .update(
          "User",
          { partyLeaderId: Number(inviterId) },
          {
            id: Number(inviterId),
          },
        )
        .run(pool)
      // console.log("set inviter's leader to inviter:", invitersLeader)

      const myLeader = await db
        .update(
          "User",
          { partyLeaderId: Number(inviterId) },
          {
            id: Number(auth.id),
          },
        )
        .run(pool)
      //console.log("set my leader to inviter:", myLeader)

      //console.log("return invite.id:", String(invite.id))
      return String(invite.id)
    } catch (e) {
      console.log("CATCH: ", e)
      //console.log(e)
    }
  },
})

export const mutationDeclineInviteToParty = t.field({
  name: "declineInviteToParty",
  type: t.ID,
  args: {
    id: t.arg(t.NonNullInput(t.ID)),
  },
  resolve: async (_, { id: inviterId }, { pool, auth }) => {
    try {
      const invite = (
        await db
          .deletes("Invite", {
            inviterId: Number(inviterId),
            inviteeId: Number(auth.id),
          })
          .run(pool)
      ).at(0)
      if (invite === undefined) return
      return String(invite)
    } catch (e) {
      console.log(e)
    }
  },
})

export const mutationCancelInviteToParty = t.field({
  name: "cancelInviteToParty",
  type: t.ID,
  args: {
    id: t.arg(t.NonNullInput(t.ID)),
  },
  resolve: async (_, { id: inviteeId }, { pool, auth }) => {
    try {
      const invite = (
        await db
          .deletes("Invite", {
            inviterId: Number(auth.id),
            inviteeId: Number(inviteeId),
          })
          .run(pool)
      ).at(0)
      // console.log("CANCEL INVITE:", invite)
      if (invite === undefined) return
      return String(invite)
    } catch (e) {
      console.log(e)
    }
  },
})

export const mutationRemoveFromParty = t.field({
  name: "removeFromParty",
  type: t.ID,
  args: {
    id: t.arg(t.NonNullInput(t.ID)),
  },
  resolve: async (_, { id }, { pool, auth }) => {
    try {
      const member = (
        await db
          .update(
            "User",
            {
              partyLeaderId: undefined,
            },
            {
              id: Number(id),
              partyLeaderId: Number(auth.id),
            },
          )
          .run(pool)
      ).at(0)
      if (member === undefined) return
      return String(member.id)
    } catch (e) {
      console.log(e)
    }
  },
})

export const mutationLeaveParty = t.field({
  name: "leaveParty",
  type: t.ID,
  resolve: async (_, _args, { pool, auth }) => {
    try {
      const me = (
        await db
          .update(
            "User",
            {
              partyLeaderId: undefined,
            },
            db.sql`${"partyLeaderId"} = ${db.param(auth.id)}
            OR ${"id"} = ${db.param(auth.id)}`,
          )
          .run(pool)
      ).at(0)
      if (me === undefined) return
      return String(me.id)
    } catch (e) {
      console.log(e)
    }
  },
})
