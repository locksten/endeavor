package com.example.endeavor.ui.todo.daily

import Daily
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import com.example.endeavor.DailiesQuery
import com.example.endeavor.gqlWatchQuery
import com.example.endeavor.ui.AppLazyColumn
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun CDailyList() {
    gqlWatchQuery(DailiesQuery())?.me?.dailies?.let { DailyList(it) }
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun DailyList(dailies: List<DailiesQuery.Daily>) {
    AppLazyColumn(fabPadding = true) {
        items(
            dailies.sortedWith(
                compareBy<DailiesQuery.Daily> { it.isCompleted }
                    .thenByDescending { (it.lastCompletionDate ?: it.createdAt) }
            ), { it.id }
        )
        { Daily(it) }
    }
}