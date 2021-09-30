import { AppContext } from "context"
import { db, dc } from "database"
import { ObjectType } from "gqtx/dist/types"
import { DateType } from "schema/date"
import { idResolver, t, typeResolver } from "schema/typesFactory"
import { User as QUser } from "zapatos/schema"

export { User as QUser } from "zapatos/schema"
export type User = QUser.JSONSelectable & { _type?: "User" }

export const UserType: ObjectType<AppContext, User | null> = t.objectType<User>(
  {
    name: "User",
    fields: () => [
      idResolver,
      typeResolver("User"),
      t.defaultField("username", t.NonNull(t.String)),
      t.defaultField("createdAt", t.NonNull(DateType)),
    ],
  },
)

export const queryUserById = t.field("userById", {
  type: UserType,
  args: {
    id: t.arg(t.NonNullInput(t.ID)),
  },
  resolve: async (_, { id }, { pool }) => {
    return await db.selectOne("User", { id: Number(id) }).run(pool)
  },
})

export const queryUserByName = t.field("userByName", {
  type: UserType,
  args: {
    username: t.arg(t.NonNullInput(t.String)),
  },
  resolve: async (_, { username }, { pool }) => {
    return await db.selectOne("User", { username }).run(pool)
  },
})

export const queryUserSearch = t.field("userSearch", {
  type: t.NonNull(t.List(t.NonNull(UserType))),
  args: {
    searchTerm: t.arg(t.NonNullInput(t.String)),
  },
  resolve: async (_, { searchTerm }, { pool }) => {
    return await db
      .select("User", { username: dc.like("%" + searchTerm + "%") })
      .run(pool)
  },
})
