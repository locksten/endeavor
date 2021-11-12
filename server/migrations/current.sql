drop table if exists "User" cascade;
create table "User" (
    "id" serial primary key,
    "username" text not null unique,
    "password" text not null,
    "createdAt" timestamp with time zone not null default now(),
    "hitpoints" integer not null,
    "maxHitpoints" integer not null,
    "energy" integer not null,
    "maxEnergy" integer not null,
    "experience" integer not null
);

drop table if exists "Habit" cascade;
create table "Habit" (
    "id" serial primary key,
    "userId" integer not null references "User" (id) on delete cascade,
    "title" text not null,
    "difficulty" integer not null,
    "positiveCount" integer not null default 0,
    "negativeCount" integer not null default 0,
    "createdAt" timestamp with time zone not null default now()
);

drop table if exists "Daily" cascade;
create table "Daily" (
    "id" serial primary key,
    "userId" integer not null references "User" (id) on delete cascade,
    "title" text not null,
    "difficulty" integer not null,
    "lastCompletionDate" timestamp with time zone,
    "createdAt" timestamp with time zone not null default now()
);

drop table if exists "Task" cascade;
create table "Task" (
    "id" serial primary key,
    "userId" integer not null references "User" (id) on delete cascade,
    "title" text not null,
    "completionDate" timestamp with time zone,
    "difficulty" integer not null,
    "createdAt" timestamp with time zone not null default now()
);