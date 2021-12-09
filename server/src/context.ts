import { ExpressContext } from "apollo-server-express/dist/ApolloServer"
import { authentication } from "schema/auth/authentication"
import { Pool } from "pg"
import { Queryable, TxnClientForSerializable } from "zapatos/db"

const pool = new Pool({
  connectionString: process.env.DATABASE,
})

export const newAppContext = (ctx: ExpressContext) => ({
  pool,
  auth: authentication(ctx),
})

export type AppContext = Omit<ReturnType<typeof newAppContext>, "pool"> & {
  pool: Pool | TxnClientForSerializable
}
