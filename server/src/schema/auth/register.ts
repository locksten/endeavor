import { hash } from "bcrypt"
import { AppContext } from "context"
import { db } from "database"
import { ObjectType } from "gqtx"
import { makeAuthTokens } from "schema/auth/authToken"
import {
  SuccessfulLoginResult,
  SuccessfulLoginResultType,
} from "schema/auth/login"
import { t, typeResolver } from "schema/typesFactory"

type FailedRegistrationResult = {
  _type: "FailedRegistrationResult"
  reason:
    | "Username is already taken"
    | "Username must be at least one character long"
    | "Password must be at least twelve characters long"
}

export const FailedRegistrationResultType: ObjectType<
  AppContext,
  FailedRegistrationResult
> = t.objectType<FailedRegistrationResult>({
  name: "FailedRegistrationResult",
  fields: () => [
    typeResolver("FailedRegistrationResult"),
    t.field({
      name: "reason",
      type: t.NonNull(t.String),
    }),
  ],
})

export const RegistrationResultType = t.unionType<
  SuccessfulLoginResult | FailedRegistrationResult
>({
  name: "RegistrationResult",
  types: [SuccessfulLoginResultType, FailedRegistrationResultType],
  resolveType: (t) => {
    if ((t as any)._type === FailedRegistrationResultType.name)
      return FailedRegistrationResultType
    return SuccessfulLoginResultType
  },
})

export const mutationRegister = t.field({
  name: "register",
  type: t.NonNull(RegistrationResultType),
  args: {
    username: t.arg(t.NonNullInput(t.String)),
    password: t.arg(t.NonNullInput(t.String)),
  },
  resolve: async (_, { username, password }, { pool }) => {
    let err: FailedRegistrationResult["reason"] | undefined = undefined

    if (password.length < 12)
      err = "Password must be at least twelve characters long"

    if (username.length < 1)
      err = "Username must be at least one character long"

    if (err) {
      const res: FailedRegistrationResult = {
        _type: "FailedRegistrationResult",
        reason: err,
      }
      return res
    }

    try {
      const user = await db
        .insert("User", {
          username,
          password: await hash(password, 10),
          hitpoints: 10,
          maxHitpoints: 10,
          energy: 0,
          maxEnergy: 10,
          experience: 0,
        })
        .run(pool)
      const res: SuccessfulLoginResult = {
        _type: "SuccessfulLoginResult",
        authTokens: makeAuthTokens(user),
        user,
      }
      return res
    } catch (e) {
      if (
        db.isDatabaseError(e, "IntegrityConstraintViolation_UniqueViolation")
      ) {
        const res: FailedRegistrationResult = {
          _type: "FailedRegistrationResult",
          reason: "Username is already taken",
        }
        return res
      }
    }
  },
})
