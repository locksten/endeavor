package com.example.endeavor.ui.battle

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import com.example.endeavor.BattleQuery
import com.example.endeavor.gqlWatchQuery
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun CBattleOrCreatureScreen() {
    val battle = (gqlWatchQuery(BattleQuery())?.me?.battle)
    if (battle == null) {
        CCreatures()
    } else {
       CBattleScreen(battle)
    }
}