import { buildGraphQLSchema } from "gqtx"
import { mutationLogin } from "schema/auth/login"
import { mutationRegister } from "schema/auth/register"
import { queryMe } from "schema/me"
import { t } from "schema/typesFactory"
import { queryUserById, queryUserByName, queryUserSearch } from "schema/user"

const query = t.queryType({
  fields: [queryMe, queryUserById, queryUserByName, queryUserSearch],
})

const mutation = t.mutationType({
  fields: () => [mutationRegister, mutationLogin],
})

export const schema = buildGraphQLSchema({
  query,
  mutation,
})
