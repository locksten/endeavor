package com.example.endeavor.ui.item

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloNetworkException
import com.example.endeavor.*
import com.example.endeavor.type.CreateTaskInput
import com.example.endeavor.ui.theme.Theme
import kotlinx.coroutines.launch

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun Item(item: InventoryQuery.Inventory) {
    MutationComposable { gql, scope ->
        val fontWeight = if (item.isEquiped) FontWeight.Bold else FontWeight.Normal
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 52.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Theme.colors.graySurface)
                .combinedClickable(onClick = {
                    scope.launch {
                        equipItem(gql, item.id)
                    }
                })
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row {
                Text(
                    text = "${item.emoji}  ",
                    color = Theme.colors.onGraySurface,
                    fontSize = 20.sp,
                )
                Text(
                    text = item.name,
                    color = Theme.colors.onGraySurface,
                    fontWeight = fontWeight,
                    fontSize = 20.sp,
                )
            }
            Row {
                item.strengthBonus?.let {
                    Text(
                        text = "🗡️ ${item.strengthBonus}",
                        color = Theme.colors.onGraySurface,
                        fontWeight = fontWeight,
                        fontSize = 20.sp,
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                item.defenseBonus?.let {
                    Text(
                        text = "🛡️ ${item.defenseBonus}",
                        color = Theme.colors.onGraySurface,
                        fontWeight = fontWeight,
                        fontSize = 20.sp,
                    )
                }
            }
        }
    }
}

suspend fun equipItem(gql: ApolloClient, id: String) {
    try {
        gql.mutate(EquipItemMutation(id)).await()
        gql.query(InventoryQuery()).await()
        gql.query(VitalsQuery()).await()
    } catch (e: ApolloNetworkException) {
    }
}