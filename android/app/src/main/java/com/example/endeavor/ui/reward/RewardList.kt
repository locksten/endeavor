package com.example.endeavor.ui.reward

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.endeavor.RewardsQuery
import com.example.endeavor.gqlWatchQuery

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun CRewardList() {
    gqlWatchQuery(RewardsQuery())?.me?.let { me ->
        Scaffold(floatingActionButton = { FloatingAddButton() }) {
            Column {
                Text(
                    text = " 💰 ${me.user.gold}",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 14.dp)
                )
                RewardList(
                    me.rewards.map {
                        Reward(
                            id = it.id,
                            title = it.title,
                            price = it.price,
                            createdAt = it.createdAt as String
                        )
                    }, me.user.gold
                )
            }
        }
    }
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun RewardList(rewards: List<Reward>, gold: Int) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        items(
            rewards.sortedWith(
                compareByDescending<Reward> { it.price }.thenBy { it.createdAt }
            )
        )

        { RewardListItem(it, gold) }
        item { Spacer(Modifier.height(80.dp)) }
    }
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun FloatingAddButton() {
    var isDialogOpen by remember { mutableStateOf(false) }
    FloatingActionButton(
        onClick = { isDialogOpen = true },
    ) {
        Icon(Icons.Filled.Add, "Add")
        if (isDialogOpen) CCreateRewardModal { isDialogOpen = false }
    }
}