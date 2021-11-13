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
