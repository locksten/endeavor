import { AppContext } from "context"
import { ObjectType } from "gqtx"
import { sign } from "jsonwebtoken"
import { AuthTokenBody, accessTokenSecret } from "schema/auth/authentication"
import { t, typeResolver } from "schema/typesFactory"
import { User } from "schema/user"

export type AuthTokens = { _type: "AuthTokens"; accessToken: string }

export const makeAuthTokens = (user: User): AuthTokens => {
  const tokenBody: AuthTokenBody = { id: user.id }
  const accessToken = sign(tokenBody, accessTokenSecret, { expiresIn: "7d" })
  return { _type: "AuthTokens", accessToken }
}

export const AuthTokensType: ObjectType<AppContext, AuthTokens> =
  t.objectType<AuthTokens>({
    name: "AuthTokens",
    fields: () => [
      typeResolver("AuthTokens"),
      t.field({ name: "accessToken", type: t.NonNull(t.String) }),
    ],
  })
