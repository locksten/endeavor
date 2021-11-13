import { compare } from "bcrypt"
import { AppContext } from "context"
import { db } from "database"
import { ObjectType } from "gqtx"
import {
  AuthTokens,
  AuthTokensType,
  makeAuthTokens,
} from "schema/auth/authToken"
import { t, typeResolver } from "schema/typesFactory"
import { User, UserType } from "schema/user"

type FailedLoginResult = {
  _type: "FailedLoginResult"
  reason: "Invalid username or password"
}

export const FailedLoginResultType: ObjectType<AppContext, FailedLoginResult> =
  t.objectType<FailedLoginResult>({
    name: "FailedLoginResult",
    fields: () => [
      typeResolver("FailedLoginResult"),
      t.field({
        name: "reason",
        type: t.NonNull(t.String),
      }),
    ],
  })

export type SuccessfulLoginResult = {
  _type: "SuccessfulLoginResult"
  user: User
  authTokens: AuthTokens
}

export const SuccessfulLoginResultType: ObjectType<
  AppContext,
  SuccessfulLoginResult
> = t.objectType<SuccessfulLoginResult>({
  name: "SuccessfulLoginResult",
  fields: () => [
    typeResolver("SuccessfulLoginResult"),
    t.field({ name: "user", type: t.NonNull(UserType) }),
    t.field({ name: "authTokens", type: t.NonNull(AuthTokensType) }),
  ],
})

export const LoginResultType = t.unionType<
  SuccessfulLoginResult | FailedLoginResult
>({
  name: "LoginResult",
  types: [SuccessfulLoginResultType, FailedLoginResultType],
  resolveType: (t) => {
    if ((t as any)._type === SuccessfulLoginResultType.name)
      return SuccessfulLoginResultType
    return FailedLoginResultType
  },
})

export const mutationLogin = t.field({
  name: "login",
  type: t.NonNull(LoginResultType),
  args: {
    username: t.arg(t.NonNullInput(t.String)),
    password: t.arg(t.NonNullInput(t.String)),
  },
  resolve: async (_, { username, password }, { pool }) => {
    const user = await db.selectOne("User", { username }).run(pool)

    if (!user || !(await compare(password, user.password))) {
      const res: FailedLoginResult = {
        _type: "FailedLoginResult",
        reason: "Invalid username or password",
      }
      return res
    }

    const res: SuccessfulLoginResult = {
      _type: "SuccessfulLoginResult",
      authTokens: makeAuthTokens(user),
      user,
    }
    return res
  },
})
