import { buildGraphQLSchema } from "gqtx"
import { mutationLogin } from "schema/auth/login"
import { mutationRegister } from "schema/auth/register"
import { queryMe } from "schema/me"
import {
  mutationCompleteTask,
  mutationCreateTask,
  mutationDeleteTask,
} from "schema/task"
import { t } from "schema/typesFactory"
import { queryUserById, queryUserByName, queryUserSearch } from "schema/user"

const queryRNG = t.field("RNG", {
  type: t.NonNull(t.Int),
  resolve: async () => {
    return Math.floor(Math.random() * 100)
  },
})

const query = t.queryType({
  fields: [queryRNG, queryMe, queryUserById, queryUserByName, queryUserSearch],
})

const mutation = t.mutationType({
  fields: () => [
    mutationRegister,
    mutationLogin,
    mutationCreateTask,
    mutationDeleteTask,
    mutationCompleteTask,
  ],
})

export const schema = buildGraphQLSchema({
  query,
  mutation,
})
