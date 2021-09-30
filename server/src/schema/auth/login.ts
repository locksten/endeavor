import { compare } from "bcrypt"
import { db } from "database"
import {
  AuthTokens,
  AuthTokensType,
  makeAuthTokens,
} from "schema/auth/authToken"
import {
  defaultStringLiteralFieldResolver,
  t,
  typeResolver,
} from "schema/typesFactory"
import { User, UserType } from "schema/user"

type FailedLoginResult = {
  _type: "FailedLoginResult"
  reason: "Invalid username or password"
}

export const FailedLoginResultType = t.objectType<FailedLoginResult>({
  name: "FailedLoginResult",
  fields: () => [
    typeResolver("FailedLoginResult"),
    defaultStringLiteralFieldResolver("reason"),
  ],
})

export type SuccessfulLoginResult = {
  _type: "SuccessfulLoginResult"
  user: User
  authTokens: AuthTokens
}

export const SuccessfulLoginResultType = t.objectType<SuccessfulLoginResult>({
  name: "SuccessfulLoginResult",
  fields: () => [
    typeResolver("SuccessfulLoginResult"),
    t.defaultField("user", t.NonNull(UserType)),
    t.defaultField("authTokens", t.NonNull(AuthTokensType)),
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

export const mutationLogin = t.field("login", {
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
