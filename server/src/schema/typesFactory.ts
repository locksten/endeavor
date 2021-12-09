import { AppContext } from "context"
import { createTypesFactory } from "gqtx"

export const t = createTypesFactory<AppContext>()

export const idResolver = t.field<"id", { id: number }, any, any>({
  name: "id",
  type: t.NonNull(t.ID),
  resolve: ({ id }) => {
    return String(id)
  },
})

export const _typeResolver = (type: string) =>
  t.field({ name: "_type", type: t.NonNull(t.String), resolve: () => type })

export const typeResolver = (type: string) =>
  t.field({ name: "type", type: t.NonNull(t.String), resolve: () => type })
