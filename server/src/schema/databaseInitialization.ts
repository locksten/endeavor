import { AppContext } from "context"
import { db } from "database"
import { QHabit } from "schema/habit"
import { createTodo } from "schema/todo"
import { t } from "schema/typesFactory"

export const mutationInitializeDatabase = t.field({
  name: "initializeDatabase",
  type: t.String,
  resolve: async (_, _args, ctx) => {
    if (!ctx.auth.id) return "Unauthorized"

    Promise.all([
      habits(ctx),
      dailies(ctx),
      tasks(ctx),
      rewards(ctx),
      creatures(ctx),
    ])

    return "Initialized"
  },
})

const rewards = async ({ auth, pool }: AppContext) => {
  const userId = Number(auth.id)
  return await db
    .insert("Reward", [
      { userId, title: "🏖️️  Day Off", price: 300 },
      { userId, title: "🍕  Delivery", price: 200 },
      { userId, title: "🍰  Dessert", price: 50 },
      { userId, title: "🎮  1h Gaming", price: 40 },
      { userId, title: "📱  20m Reddit", price: 20 },
      { userId, title: "📺  20m TV", price: 30 },
    ])
    .run(pool)
}

const creatures = async ({ pool }: AppContext) =>
  await db
    .insert("Creature", [
      { strength: 1, maxHitpoints: 10, emoji: "🐀", name: "Rat" },
      { strength: 2, maxHitpoints: 20, emoji: "🦀", name: "Crab" },
      { strength: 3, maxHitpoints: 70, emoji: "🐺", name: "Wolf" },
      { strength: 5, maxHitpoints: 80, emoji: "🐊", name: "Crocodile" },
      { strength: 8, maxHitpoints: 100, emoji: "🦁", name: "Lion" },
      { strength: 10, maxHitpoints: 80, emoji: "🐅", name: "Tiger" },
      { strength: 15, maxHitpoints: 150, emoji: "🦈", name: "Shark" },
      { strength: 30, maxHitpoints: 300, emoji: "🐲", name: "Dragon" },
    ])
    .run(pool)

const tasks = async (ctx: AppContext) =>
  Promise.all([
    createTodo(ctx, "Task", {}, { title: "Work on app", difficulty: 100 }),
    createTodo(ctx, "Task", {}, { title: "Make appointment", difficulty: 100 }),
    createTodo(ctx, "Task", {}, { title: "Send email", difficulty: 100 }),
  ])

const dailies = async (ctx: AppContext) =>
  Promise.all([
    createTodo(ctx, "Daily", {}, { title: "🏠  Do Chores", difficulty: 70 }),
    createTodo(ctx, "Daily", {}, { title: "🐕  Walk the Dog", difficulty: 30 }),
    createTodo(ctx, "Daily", {}, { title: "🍲  Cook", difficulty: 20 }),
    createTodo(ctx, "Daily", {}, { title: "🏃  Exercise", difficulty: 100 }),
    createTodo(ctx, "Daily", {}, { title: "👄  Floss", difficulty: 10 }),
    createTodo(ctx, "Daily", {}, { title: "👶  Do Skincare", difficulty: 80 }),
    createTodo(
      ctx,
      "Daily",
      {},
      { title: "🍬  Take Vitamins", difficulty: 20 },
    ),
    createTodo(ctx, "Daily", {}, { title: "🃏  Review Anki", difficulty: 100 }),
  ])

const habits = (ctx: AppContext) =>
  Promise.all([
    createTodo(
      ctx,
      "Habit",
      { negativeCount: null, positiveCount: 53 } as Omit<
        QHabit.Insertable,
        "id"
      >,
      {
        title: "Drink Water 💧",
        difficulty: 30,
      },
    ),
    createTodo(
      ctx,
      "Habit",
      { negativeCount: null, positiveCount: 5 } as Omit<
        QHabit.Insertable,
        "id"
      >,
      {
        title: "Read 📘",
        difficulty: 80,
      },
    ),
    createTodo(
      ctx,
      "Habit",
      { negativeCount: null, positiveCount: 7 } as Omit<
        QHabit.Insertable,
        "id"
      >,
      {
        title: "Try Something New 🆕",
        difficulty: 60,
      },
    ),
    createTodo(
      ctx,
      "Habit",
      { negativeCount: null, positiveCount: 12 } as Omit<
        QHabit.Insertable,
        "id"
      >,
      {
        title: "Study 🎓",
        difficulty: 80,
      },
    ),
    createTodo(
      ctx,
      "Habit",
      { negativeCount: 83, positiveCount: 141 } as Omit<
        QHabit.Insertable,
        "id"
      >,
      {
        title: "🛏️ Sleep Early 🛌",
        difficulty: 100,
      },
    ),
    createTodo(
      ctx,
      "Habit",
      { negativeCount: 42, positiveCount: 185 } as Omit<
        QHabit.Insertable,
        "id"
      >,
      {
        title: "🍔 Eat Healthy 🥗",
        difficulty: 30,
      },
    ),
    createTodo(
      ctx,
      "Habit",
      { negativeCount: 0, positiveCount: 73 } as Omit<QHabit.Insertable, "id">,
      {
        title: "🚗 Walk 🚶",
        difficulty: 20,
      },
    ),
    createTodo(
      ctx,
      "Habit",
      { negativeCount: 19, positiveCount: 5 } as Omit<QHabit.Insertable, "id">,
      {
        title: "️🛌 Get Up Early 🛏️",
        difficulty: 100,
      },
    ),
  ])
