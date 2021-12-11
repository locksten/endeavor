package com.example.endeavor.ui.battle

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import com.example.endeavor.BattleQuery

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun CBattleScreen(battle: BattleQuery.Battle) {
    battle.creature?.let { creature ->
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            BattleCreature(creature, battle.creatureHitpoints)
            BattlePartyMembers(battle.partyMembers)
        }
    }
}