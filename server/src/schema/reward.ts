import { AppContext } from "context"
import { db } from "database"

export const giveRewardForTodo = async (
  { auth, pool }: AppContext,
  difficulty: number,
) => {
  await db
    .update(
      "User",
      {
        energy: db.sql`LEAST(${"maxEnergy"}, ${db.self} + ${db.param(
          difficulty,
        )})`,
        hitpoints: db.sql`LEAST(${"maxHitpoints"}, ${db.self} + ${db.param(
          difficulty,
        )})`,
        experience: db.sql`LEAST(1000, ${db.self} + ${db.param(difficulty)})`,
      },
      { id: Number(auth.id) },
    )
    .run(pool)
}

export const givePenaltyForTodo = async (
  { auth, pool }: AppContext,
  difficulty: number,
) => {
  const user = (
    await db
      .update(
        "User",
        {
          hitpoints: db.sql`GREATEST(${db.param(0)}, ${db.self} - ${db.param(
            difficulty,
          )})`,
        },
        { id: Number(auth.id) },
      )
      .run(pool)
  ).at(0)

  if (user === undefined) return undefined

  if (user.hitpoints === 0) {
    console.log("DEAD")
  }
}
