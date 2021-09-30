import { AppContext } from "context"
import { createTypesFactory } from "gqtx"

export const t = createTypesFactory<AppContext>()

export const idResolver = t.field<{ id: number }, any, any>("id", {
  type: t.NonNull(t.ID),
  resolve: ({ id }) => {
    return String(id)
  },
})

export const typeResolver = (type: string) =>
  t.field<any, {}, string>("_type", {
    type: t.NonNull(t.String),
    resolve: () => type,
  })

export const defaultStringLiteralFieldResolver = <
  T extends object,
  K extends keyof T
>(
  key: K,
) => t.defaultField<T, K>(key, t.NonNull(t.String) as any)
