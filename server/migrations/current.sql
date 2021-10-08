drop table if exists "User" cascade;
create table "User" (
    "id" serial primary key,
    "username" text not null unique,
    "password" text not null,
    "createdAt" timestamp with time zone default now()
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