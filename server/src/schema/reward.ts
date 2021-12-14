import { AppContext } from "context"
import { db, dc } from "database"
import { sendNotification } from "firebaseMessaging"
import { ObjectType } from "gqtx"
import { Battle } from "schema/battle"
import { Creature } from "schema/creature"
import { DateType } from "schema/date"
import { idResolver, t, _typeResolver } from "schema/typesFactory"
import {
  experienceFromLevel,
  levelFromExperience,
  QUser,
  User,
} from "schema/user"
import { isObjectEmpty } from "utils"
import { Reward as QReward } from "zapatos/schema"

export { Reward as QReward } from "zapatos/schema"
export type Reward = QReward.JSONSelectable

export const RewardType: ObjectType<AppContext, Reward> = t.objectType<Reward>({
  name: "Reward",
  fields: () => [
    idResolver,
    _typeResolver("Reward"),
    t.field({ name: "title", type: t.NonNull(t.String) }),
    t.field({ name: "createdAt", type: t.NonNull(DateType) }),
    t.field({ name: "price", type: t.NonNull(t.Int) }),
  ],
})

export const createRewardInput = t.inputObjectType({
  name: "CreateRewardInput",
  fields: () => ({
    title: t.arg(t.NonNullInput(t.String)),
    price: t.arg(t.NonNullInput(t.Int)),
  }),
})

export const mutationCreateReward = t.field({
  name: "createReward",
  type: RewardType,
  args: {
    createRewardInput: t.arg(t.NonNullInput(createRewardInput)),
  },
  resolve: async (_, { createRewardInput: input }, { pool, auth }) => {
    if (!auth.id) return

    if (input.price < 1) return
    const reward: QReward.Insertable = {
      userId: auth.id,
      ...input,
    }

    try {
      return await db.insert("Reward", reward).run(pool)
    } catch (e) {
      console.log(e)
    }
  },
})

export const mutationDeleteReward = t.field({
  name: "deleteReward",
  type: t.ID,
  args: {
    id: t.arg(t.NonNullInput(t.ID)),
  },
  resolve: async (_, { id }, { pool, auth }) => {
    const deletedIds = await db
      .deletes(
        "Reward",
        { userId: auth.id, id: Number(id) },
        { returning: ["id"] },
      )
      .run(pool)
    return deletedIds.at(0) !== undefined ? String(deletedIds[0].id) : undefined
  },
})

export const mutationBuyReward = t.field({
  name: "buyReward",
  type: t.ID,
  args: {
    id: t.arg(t.NonNullInput(t.ID)),
  },
  resolve: async (_, { id }, ctx) => {
    const { pool, auth } = ctx

    const reward = await db.selectOne("Reward", { id: Number(id) }).run(pool)
    if (reward === undefined) return

    const user = (
      await db
        .update(
          "User",
          { gold: db.sql`${db.self} - ${db.param(reward.price)}` },
          {
            id: auth.id,
            gold: dc.gte(reward.price),
          },
        )
        .run(pool)
    ).at(0)

    if (user === undefined) return

    return String(reward.id)
  },
})

export const updateRewardInput = t.inputObjectType({
  name: "UpdateRewardInput",
  fields: () => ({
    id: t.arg(t.NonNullInput(t.ID)),
    title: t.arg(t.String),
    price: t.arg(t.Int),
  }),
})

export const mutationUpdateReward = t.field({
  name: "updateReward",
  type: RewardType,
  args: {
    updateRewardInput: t.arg(t.NonNullInput(updateRewardInput)),
  },
  resolve: async (
    _,
    { updateRewardInput: { id, price, title } },
    { pool, auth },
  ) => {
    const patch: QReward.Updatable = {
      ...(title === null || title === undefined ? undefined : { title }),
      ...(price === null || price === undefined ? undefined : { price }),
    }
    if (isObjectEmpty(patch)) return

    return (
      await db
        .update("Reward", patch, {
          id: Number(id),
          userId: auth.id,
        })
        .run(pool)
    ).at(0)
  },
})
