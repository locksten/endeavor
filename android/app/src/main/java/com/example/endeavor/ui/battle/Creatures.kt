package com.example.endeavor.ui.battle

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.unit.dp
import com.example.endeavor.CreaturesQuery
import com.example.endeavor.gqlWatchQuery
import com.example.endeavor.ui.AppLazyColumn
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun CCreatures() {
    (gqlWatchQuery(CreaturesQuery())?.creatures)?.let { creatures ->
        AppLazyColumn(spacedBy = 8.dp) {
            items(creatures, { it.id }) { CreatureListItem(it) }
        }
    }
}