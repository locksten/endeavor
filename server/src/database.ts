import * as database from "zapatos/db"
import { conditions } from "zapatos/db"

export const db = database

const incrementNullable = db.sql`COALESCE(${"maxEnergy"}, 0)  + ${db.param(1)}`

export const dc = { ...conditions, incrementNullable }
