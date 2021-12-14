import { AppContext } from "context"
import { db, dc } from "database"
import { auth } from "firebase-admin"
import { ObjectType } from "gqtx"
import { DateType } from "schema/date"
import { idResolver, t, _typeResolver } from "schema/typesFactory"
import { User as QUser } from "zapatos/schema"

export { User as QUser } from "zapatos/schema"
export type User = QUser.JSONSelectable

const experienceConstant = 0.25

export const levelFromExperience = (xp: number) =>
  Math.floor(experienceConstant * Math.sqrt(xp)) + 1

export const experienceFromLevel = (level: number) =>
  Math.ceil(Math.pow((level - 1) / experienceConstant, 2))

export const experienceInCurrentLevel = (xp: number) =>
  xp - experienceFromLevel(levelFromExperience(xp))

export const experienceForNextLevel = (xp: number) =>
  experienceFromLevel(levelFromExperience(xp) + 1) -
  experienceFromLevel(levelFromExperience(xp))

export const maxEnergyFromLevel = (level: number) => level * 3
export const maxHitpointsFromLevel = (level: number) => level * 10

export const UserType: ObjectType<AppContext, User | null> = t.objectType<User>(
  {
    name: "User",
    fields: () => [
      idResolver,
      _typeResolver("User"),
      t.field({ name: "username", type: t.NonNull(t.String) }),
      t.field({ name: "createdAt", type: t.NonNull(DateType) }),
      t.field({
        name: "isPartyLeader",
        type: t.NonNull(t.Boolean),
        resolve: async ({ id, partyLeaderOrUserId }, _args, _ctx) => {
          return id === partyLeaderOrUserId
        },
      }),
      t.field({ name: "hitpoints", type: t.NonNull(t.Int) }),
      t.field({ name: "maxHitpoints", type: t.NonNull(t.Int) }),
      t.field({ name: "energy", type: t.NonNull(t.Int) }),
      t.field({ name: "maxEnergy", type: t.NonNull(t.Int) }),
      t.field({
        name: "level",
        type: t.NonNull(t.Int),
        resolve: async ({ experience }, _args, _ctx) => {
          return levelFromExperience(experience)
        },
      }),
      t.field({
        name: "experienceInCurrentLevel",
        type: t.NonNull(t.Int),
        resolve: async ({ experience }, _args, _ctx) => {
          return experienceInCurrentLevel(experience)
        },
      }),
      t.field({
        name: "experienceForNexLevel",
        type: t.NonNull(t.Int),
        resolve: async ({ experience }, _args, _ctx) => {
          return experienceForNextLevel(experience)
        },
      }),
      t.field({ name: "gold", type: t.NonNull(t.Int) }),
    ],
  },
)

export const mutationUpdateFirebaseToken = t.field({
  name: "updateFirebaseToken",
  type: UserType,
  args: {
    token: t.arg(t.String),
  },
  resolve: async (_, { token }, { auth, pool }) => {
    if (!auth.id) return
    return await db.serializable(pool, async (txnClient) => {
      if (token) {
        await db
          .update("User", { firebaseToken: null }, { firebaseToken: token })
          .run(txnClient)
      }
      return (
        await db
          .update("User", { firebaseToken: token }, { id: auth.id })
          .run(txnClient)
      ).at(0)
    })
  },
})

export const queryUserById = t.field({
  name: "userById",
  type: UserType,
  args: {
    id: t.arg(t.NonNullInput(t.ID)),
  },
  resolve: async (_, { id }, { pool }) => {
    return await db.selectOne("User", { id: Number(id) }).run(pool)
  },
})

export const queryUserByName = t.field({
  name: "userByName",
  type: UserType,
  args: {
    username: t.arg(t.NonNullInput(t.String)),
  },
  resolve: async (_, { username }, { pool }) => {
    return await db.selectOne("User", { username }).run(pool)
  },
})

export const queryUserSearch = t.field({
  name: "userSearch",
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
