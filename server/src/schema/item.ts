import { AppContext } from "context"
import { db, dc } from "database"
import { ObjectType } from "gqtx"
import { isTypeSystemDefinitionNode } from "graphql"
import { DateType } from "schema/date"
import { idResolver, t, _typeResolver } from "schema/typesFactory"
import { QUser, User } from "schema/user"
import { update } from "zapatos/db"
import { Item as QItem, UserItem } from "zapatos/schema"

export { Item as QItem } from "zapatos/schema"
export type Item = QItem.JSONSelectable

export const ItemType: ObjectType<AppContext, Item> = t.objectType<Item>({
  name: "Item",
  fields: () => [
    idResolver,
    _typeResolver("Item"),
    t.field({ name: "name", type: t.NonNull(t.String) }),
    t.field({ name: "emoji", type: t.NonNull(t.String) }),
    t.field({ name: "defenseBonus", type: t.Int }),
    t.field({ name: "strengthBonus", type: t.Int }),
    t.field({ name: "createdAt", type: t.NonNull(DateType) }),
    t.field({
      name: "isEquiped",
      type: t.NonNull(t.Boolean),
      resolve: async ({ id }, {}, { pool, auth }) => {
        const user = await db.selectOne("User", { id: auth.id }).run(pool)
        if (user === undefined) return false

        return (
          id === user.accessorySlot ||
          id === user.offenseSlot ||
          id === user.defenseSlot
        )
      },
    }),
  ],
})

export const mutationEquipItem = t.field({
  name: "equipItem",
  type: t.ID,
  args: {
    id: t.arg(t.NonNullInput(t.ID)),
  },
  resolve: async (_, { id }, ctx) => {
    const { pool, auth } = ctx

    const item = await db.selectOne("Item", { id: Number(id) }).run(pool)
    if (item === undefined) return

    const userItem = await db
      .selectOne("UserItem", { itemId: Number(id), userId: auth.id })
      .run(pool)
    if (userItem === undefined) return

    let update: QUser.Updatable = {}
    switch (item.slot) {
      case "Offense":
        update = { offenseSlot: item.id }
        break
      case "Defense":
        update = { defenseSlot: item.id }
        break
      case "Accessory":
        update = { accessorySlot: item.id }
        break
    }
    if (update === undefined) return

    const user = (
      await db.update("User", update, { id: auth.id }).run(pool)
    ).at(0)
    if (user === undefined) return

    return String(item.id)
  },
})
