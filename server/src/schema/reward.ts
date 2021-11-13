import { AppContext } from "context"
import { db } from "database"
import { User } from "schema/user"

export const giveRewardForTodo = async (
  { auth, pool }: AppContext,
  difficulty: number,
) => {
  await db
    .update(
      "User",
      {
        energy: db.sql`LEAST(${"maxEnergy"}, ${db.self} + ${db.param(
          difficulty / 10,
        )})`,
        hitpoints: db.sql`LEAST(${"maxHitpoints"}, ${db.self} + ${db.param(
          difficulty / 10,
        )})`,
        experience: db.sql`LEAST(1000, ${db.self} + ${db.param(
          difficulty / 2,
        )})`,
      },
      { id: Number(auth.id) },
    )
    .run(pool)
}

export const givePenaltyForTodo = async (
  ctx: AppContext,
  difficulty: number,
) => {
  const { auth, pool } = ctx

  const user = (
    await db
      .update(
        "User",
        {
          hitpoints: db.sql`GREATEST(${db.param(0)}, ${db.self} - ${db.param(
            difficulty / 10,
          )})`,
        },
        { id: Number(auth.id) },
      )
      .run(pool)
  ).at(0)

  if (user === undefined) return

  if (user.hitpoints === 0) await die(ctx)
}

const die = async ({ auth, pool }: AppContext) => {
  await db
    .update(
      "User",
      { experience: 0, hitpoints: 5, energy: 0 },
      { id: Number(auth.id) },
    )
    .run(pool)
}
