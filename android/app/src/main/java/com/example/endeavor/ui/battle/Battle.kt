package com.example.endeavor.ui.battle

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloNetworkException
import com.example.endeavor.*
import com.example.endeavor.ui.theme.Theme
import com.example.endeavor.ui.todo.task.CUpdateTaskModal
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun CBattleScreen(battle: BattleQuery.Battle) {
    battle.creature?.let { creature ->
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            BattleCreature(creature, battle.creatureHitpoints)
            Column {
                CAbilities(creature)
                Spacer(Modifier.height(8.dp))
                BattlePartyMembers(battle.partyMembers)
            }
        }
    }
}

@ExperimentalComposeUiApi
@ExperimentalCoroutinesApi
@Composable
fun CAbilities(creature: BattleQuery.Creature) {
    MutationComposable { gql, scope ->
        var isDialogOpen by remember { mutableStateOf(false) }
        val vitalsResponse = gqlWatchQuery(VitalsQuery())?.me?.user
        vitalsResponse?.let { vitals ->
            Row(
                modifier = Modifier.padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AbilityButton("Special Attack", vitals.maxEnergy / 2, vitals.energy) {
                    isDialogOpen = true
                }
                AbilityButton("Party Heal", vitals.maxEnergy, vitals.energy) {
                    scope.launch { usePartyHeal(gql) }
                }
                if (isDialogOpen) {
                    SpecialAttackMinigame(creature.emoji) { multiplier ->
                        isDialogOpen = false
                        scope.launch { useSpecialAttack(gql, multiplier) }
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.AbilityButton(
    name: String,
    energyCost: Int,
    energy: Int,
    onClick: () -> Unit
) {
    Button(
        modifier = Modifier.weight(0.5f),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp),
        onClick = onClick, enabled = energy >= energyCost
    ) {
        Text(
            text = "$energyCost  ",
            color = Theme.colors.energy,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            maxLines = 1,
        )
        Text(
            text = name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

suspend fun useSpecialAttack(gql: ApolloClient, multiplier: Float) {
    try {
        gql.mutate(UseSpecialAttackMutation(multiplier.toDouble())).await()
        gql.query(BattleQuery()).await()
        gql.query(VitalsQuery()).await()
    } catch (e: ApolloNetworkException) {
    }
}

suspend fun usePartyHeal(gql: ApolloClient) {
    try {
        gql.mutate(UsePartyHealMutation()).await()
        gql.query(BattleQuery()).await()
        gql.query(VitalsQuery()).await()
    } catch (e: ApolloNetworkException) {
    }
}