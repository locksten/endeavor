drop table if exists "User" cascade;
create table "User" (
    "id" serial primary key,
    "username" text not null unique,
    "password" text not null,
    "createdAt" timestamp with time zone not null default now(),
    "partyLeaderId" integer references "User" (id) on delete cascade,
    "gold" integer not null,
    "hitpoints" integer not null,
    "maxHitpoints" integer not null,
    "energy" integer not null,
    "maxEnergy" integer not null,
    "experience" integer not null
);

drop table if exists "Invite" cascade;
create table "Invite" (
    "id" serial primary key,
    "inviterId" integer not null references "User" (id) on delete cascade,
    "inviteeId" integer not null references "User" (id) on delete cascade,
    "createdAt" timestamp with time zone not null default now(),
    unique ("inviterId", "inviteeId"),
    CHECK ("inviterId" <> "inviteeId")
);

drop type if exists "TodoType";
create type "TodoType" AS ENUM ('Habit', 'Daily', 'Task');

drop table if exists "Todo" cascade;
create table "Todo" (
    "id" serial primary key,
    "type" "TodoType" not null,
    "userId" integer not null references "User" (id) on delete cascade,
    "title" text not null,
    "difficulty" integer not null,
    "createdAt" timestamp with time zone not null default now()
);


drop table if exists "Habit" cascade;
create table "Habit" (
    "id" integer not null references "Todo" (id) on delete cascade,
    "positiveCount" integer,
    "negativeCount" integer
);

drop view if exists "TodoHabit";
create view "TodoHabit" AS
select * from "Todo" join "Habit" using("id");

drop table if exists "Daily" cascade;
create table "Daily" (
    "id" integer not null references "Todo" (id) on delete cascade,
    "lastCompletionDate" timestamp with time zone
);

drop view if exists "TodoDaily";
create view "TodoDaily" AS
select * from "Todo" join "Daily" using("id");

drop table if exists "Task" cascade;
create table "Task" (
    "id" integer not null references "Todo" (id) on delete cascade,
    "completionDate" timestamp with time zone
);

drop view if exists "TodoTask";
create view "TodoTask" AS
select * from "Todo" join "Task" using("id");

drop table if exists "Reward" cascade;
create table "Reward" (
    "id" serial primary key,
    "userId" integer not null references "User" (id) on delete cascade,
    "title" text not null,
    "price" integer not null,
    "createdAt" timestamp with time zone not null default now()
);