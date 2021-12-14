package com.example.endeavor.ui.battle

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloNetworkException
import com.example.endeavor.BattleQuery
import com.example.endeavor.CreateBattleMutation
import com.example.endeavor.CreaturesQuery
import com.example.endeavor.MutationComposable
import com.example.endeavor.type.CreateBattleInput
import com.example.endeavor.ui.theme.Theme
import kotlinx.coroutines.launch

@ExperimentalFoundationApi
@Composable
fun CreatureListItem(creature: CreaturesQuery.Creature) {
    MutationComposable { gql, scope ->
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Theme.colors.graySurface)
                .combinedClickable(
                    onClick = {
                        scope.launch {
                            createBattle(gql, CreateBattleInput(creature.id))
                        }
                    },
                )
                .padding(16.dp),
            Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = creature.emoji,
                    fontSize = 50.sp,
                )
                Text(
                    text = creature.name,
                    color = Theme.colors.onGraySurface,
                    fontSize = 25.sp,
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (creature.victoryCount > 0) {
                    Text(
                        text = "${creature.victoryCount}️ 🏆",
                        fontWeight = FontWeight.Bold,
                        color = Theme.colors.onSurface,
                        fontSize = 20.sp
                    )
                    Spacer(Modifier.width(16.dp))
                }
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("${creature.maxHitpoints} ♥️")
                    Spacer(Modifier.height(4.dp))
                    Text("${creature.strength} 🗡️")
                }
            }
        }
    }
}

suspend fun createBattle(gql: ApolloClient, createBattleInput: CreateBattleInput) {
    try {
        gql.mutate(
            CreateBattleMutation(createBattleInput)
        ).await()
        gql.query(BattleQuery()).await()
    } catch (e: ApolloNetworkException) {
    }
}