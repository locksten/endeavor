import { buildGraphQLSchema } from "gqtx"
import { mutationLogin } from "schema/auth/login"
import { mutationRegister } from "schema/auth/register"
import {
  mutationCreateDaily,
  mutationDeleteDaily,
  mutationCompleteDaily,
  mutationUpdateDaily,
} from "schema/daily"
import {
  mutationCreateHabit,
  mutationDeleteHabit,
  mutationDoNegativeHabit,
  mutationDoPositiveHabit,
  mutationUpdateHabit,
} from "schema/habit"
import {
  mutationAcceptInviteToParty,
  mutationCancelInviteToParty,
  mutationDeclineInviteToParty,
  mutationInviteToParty,
  mutationLeaveParty,
  mutationRemoveFromParty,
} from "schema/invite"
import { queryMe } from "schema/me"
import {
  mutationCompleteTask,
  mutationCreateTask,
  mutationDeleteTask,
  mutationUpdateTask,
} from "schema/task"
import { t } from "schema/typesFactory"
import { queryUserById, queryUserByName, queryUserSearch } from "schema/user"

const queryRNG = t.field({
  name: "RNG",
  type: t.NonNull(t.Int),
  resolve: async () => {
    return Math.floor(Math.random() * 100)
  },
})

const query = t.queryType({
  fields: () => [
    queryRNG,
    queryMe,
    queryUserById,
    queryUserByName,
    queryUserSearch,
  ],
})

const mutation = t.mutationType({
  fields: () => [
    mutationRegister,
    mutationLogin,
    mutationCreateTask,
    mutationDeleteTask,
    mutationCompleteTask,
    mutationUpdateTask,
    mutationCreateDaily,
    mutationDeleteDaily,
    mutationCompleteDaily,
    mutationUpdateDaily,
    mutationCreateHabit,
    mutationDeleteHabit,
    mutationUpdateHabit,
    mutationDoPositiveHabit,
    mutationDoNegativeHabit,
    mutationInviteToParty,
    mutationAcceptInviteToParty,
    mutationLeaveParty,
    mutationRemoveFromParty,
    mutationDeclineInviteToParty,
    mutationCancelInviteToParty,
  ],
})

export const schema = buildGraphQLSchema({
  query,
  mutation,
})
