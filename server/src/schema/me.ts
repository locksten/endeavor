import { db } from "database"
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
  ],
})

export const queryMe = t.field("me", {
  type: MeType,
  resolve: (_, _args, { auth }) => {
    return auth.id ? { id: auth.id } : null
  },
})
