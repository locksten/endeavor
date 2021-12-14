import { buildGraphQLSchema } from "gqtx"
import { mutationLogin } from "schema/auth/login"
import { mutationRegister } from "schema/auth/register"
import {
  mutationCreateBattle,
  mutationUsePartyHeal,
  mutationUseSpecialAttack,
} from "schema/battle"
import { mutationCreateCreature, queryCreatures } from "schema/creature"
import {
  mutationCreateDaily,
  mutationCompleteDaily,
  mutationUpdateDaily,
} from "schema/daily"
import {
  mutationCreateHabit,
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
import { mutationEquipItem } from "schema/item"
import { queryMe } from "schema/me"
import {
  mutationBuyReward,
  mutationCreateReward,
  mutationDeleteReward,
  mutationUpdateReward,
} from "schema/reward"
import {
  mutationCompleteTask,
  mutationCreateTask,
  mutationUpdateTask,
} from "schema/task"
import { mutationDeleteTodo } from "schema/todo"
import { t } from "schema/typesFactory"
import {
  mutationUpdateFirebaseToken,
  queryUserById,
  queryUserByName,
  queryUserSearch,
} from "schema/user"

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
    queryCreatures,
  ],
})

const mutation = t.mutationType({
  fields: () => [
    mutationRegister,
    mutationLogin,
    mutationUpdateFirebaseToken,
    mutationCreateTask,
    mutationDeleteTodo,
    mutationCompleteTask,
    mutationUpdateTask,
    mutationCreateDaily,
    mutationCompleteDaily,
    mutationUpdateDaily,
    mutationCreateHabit,
    mutationUpdateHabit,
    mutationDoPositiveHabit,
    mutationDoNegativeHabit,
    mutationInviteToParty,
    mutationAcceptInviteToParty,
    mutationLeaveParty,
    mutationRemoveFromParty,
    mutationDeclineInviteToParty,
    mutationCancelInviteToParty,
    mutationCreateReward,
    mutationDeleteReward,
    mutationUpdateReward,
    mutationBuyReward,
    mutationCreateCreature,
    mutationCreateBattle,
    mutationUseSpecialAttack,
    mutationUsePartyHeal,
    mutationEquipItem,
  ],
})

export const schema = buildGraphQLSchema({
  query,
  mutation,
  types: [],
})
