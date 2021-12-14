package com.example.endeavor.ui.item

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.endeavor.InventoryQuery
import com.example.endeavor.VitalsQuery
import com.example.endeavor.gqlWatchQuery
import com.example.endeavor.ui.AppLazyColumn
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun CInventoryList() {
    val inventory = gqlWatchQuery(InventoryQuery())?.me?.inventory
    val vitals = gqlWatchQuery(VitalsQuery())?.me
    if (inventory != null && vitals != null) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = vitals.totalEquipmentStats,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 14.dp)
            )
            InventoryList(inventory)
        }
    }
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun InventoryList(items: List<InventoryQuery.Inventory>) {
    AppLazyColumn {
        items(
            items.sortedWith(
                compareByDescending<InventoryQuery.Inventory> { it.isEquiped }
                    .thenBy { (it.strengthBonus ?: 0) + (it.defenseBonus ?: 0) }
                    .thenBy { it.name }
            ), { it.id }
        ) { Item(it) }
    }
}