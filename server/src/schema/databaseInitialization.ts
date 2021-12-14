import { hash } from "bcrypt"
import { AppContext } from "context"
import { db } from "database"
import { Pool } from "pg"
import { QHabit } from "schema/habit"
import { createTodo } from "schema/todo"
import {
  experienceForNextLevel,
  experienceFromLevel,
  maxEnergyFromLevel,
  maxHitpointsFromLevel,
} from "schema/user"

export const initializeDbIfNotInitialized = async (pool: Pool) => {
  const isInitialized = (await db.select("User", db.all).run(pool)).length > 0
  if (isInitialized) return

  console.log(`📨 Database Initializing`)

  await creatures(pool)
  await items(pool)

  const alice = await user(pool, undefined, "Alice", 4, 0.7, 0.8)
  const luz = await user(pool, alice.id, "Luz", 4, 0.4, 1)
  const amity = await user(pool, alice.id, "Amity", 4, 0.6, 0.7)
  const willow = await user(pool, alice.id, "Willow", 4, 0.9, 0.6)
  const gus = await user(pool, alice.id, "Gus", 2, 0.8, 0.7)

  await db.insert("UserItem", { userId: 1, itemId: 1 }).run(pool)
  await db.insert("UserItem", { userId: 1, itemId: 5 }).run(pool)
  await db.insert("UserItem", { userId: 1, itemId: 7 }).run(pool)
  await db.insert("UserItem", { userId: 1, itemId: 9 }).run(pool)

  const users = [alice, luz, amity, willow, gus]

  users.forEach((user) => {
    const ctx = { pool, auth: { id: user.id } }
    Promise.all([habits(ctx), dailies(ctx), tasks(ctx), rewards(ctx)])
  })

  console.log(`🚀 Database Initialized`)
}

const user = async (
  pool: Pool,
  partyLeaderId: number | undefined,
  username: string,
  level: number,
  hitpointPercent: number,
  energyPercent: number,
) => {
  return await db
    .insert("User", {
      username,
      password: await hash("passwordpassword", 10),
      partyLeaderId: partyLeaderId,
      hitpoints: Math.floor(maxHitpointsFromLevel(level) * hitpointPercent),
      maxHitpoints: maxHitpointsFromLevel(level),
      energy: Math.floor(maxEnergyFromLevel(level) * energyPercent),
      maxEnergy: maxEnergyFromLevel(level),
      experience:
        experienceFromLevel(level) +
        Math.floor(experienceForNextLevel(experienceFromLevel(level)) / 3),
      gold: 50,
    })
    .run(pool)
}

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

const items = async (pool: Pool) =>
  await db
    .insert("Item", [
      {
        emoji: "💍",
        name: "Ring",
        defenseBonus: 2,
        strengthBonus: 2,
        slot: "Accessory",
      },
      {
        emoji: "🎀",
        name: "Ribbon",
        defenseBonus: 3,
        strengthBonus: 1,
        slot: "Accessory",
      },
      {
        emoji: "👓",
        name: "Glasses",
        defenseBonus: 1,
        strengthBonus: 3,
        slot: "Accessory",
      },
      {
        emoji: "📌",
        name: "Pushpin",
        defenseBonus: null,
        strengthBonus: 1,
        slot: "Offense",
      },
      {
        emoji: "✂️",
        name: "Scrissors",
        defenseBonus: null,
        strengthBonus: 5,
        slot: "Offense",
      },
      {
        emoji: "🖋️",
        name: "The Pen",
        defenseBonus: null,
        strengthBonus: 9,
        slot: "Offense",
      },
      {
        emoji: "🔫",
        name: "Pistol",
        defenseBonus: null,
        strengthBonus: 10,
        slot: "Offense",
      },
      {
        emoji: "👚",
        name: "Cotton T-Shirt",
        defenseBonus: 1,
        strengthBonus: null,
        slot: "Defense",
      },
      {
        emoji: "👘",
        name: "Kimono",
        defenseBonus: 7,
        strengthBonus: null,
        slot: "Defense",
      },
    ])
    .run(pool)

const creatures = async (pool: Pool) =>
  await db
    .insert("Creature", [
      { strength: 1, maxHitpoints: 10, emoji: "🐀", name: "Rat" },
      { strength: 2, maxHitpoints: 30, emoji: "🦀", name: "Crab" },
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
