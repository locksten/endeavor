drop table if exists "User" cascade;
create table "User" (
    "id" serial primary key,
    "username" text not null unique,
    "password" text not null,
    "createdAt" timestamp with time zone default now()
);