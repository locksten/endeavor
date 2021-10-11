drop table if exists "User" cascade;
create table "User" (
    "id" serial primary key,
    "username" text not null unique,
    "password" text not null,
    "createdAt" timestamp with time zone default now(),
    "hitpoints" integer not null,
    "maxHitpoints" integer not null,
    "energy" integer not null,
    "maxEnergy" integer not null,
    "experience" integer not null
);

drop table if exists "Todo" cascade;
create table "Todo" (
    "id" serial primary key,
    "userId" integer not null references "User" (id) on delete cascade,
    "title" text not null,
    "difficulty" integer not null,
    "createdAt" timestamp with time zone default now()
);

drop table if exists "Task" cascade;
create table "Task" (
    "id" integer not null references "Todo" (id) on delete cascade,
    "isCompleted" boolean not null,
    UNIQUE(id)
);