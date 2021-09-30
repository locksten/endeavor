import { ExpressContext } from "apollo-server-express/dist/ApolloServer"
import { authentication } from "schema/auth/authentication"
import { Pool } from "pg"

const pool = new Pool({
  connectionString: process.env.DATABASE,
})

export const newAppContext = (ctx: ExpressContext) => ({
  pool,
  auth: authentication(ctx),
})

export type AppContext = ReturnType<typeof newAppContext>
