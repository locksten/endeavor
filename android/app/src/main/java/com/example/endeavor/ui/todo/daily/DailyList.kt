package com.example.endeavor.ui.todo.daily

import Daily
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.endeavor.DailiesQuery
import com.example.endeavor.gqlWatchQuery

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
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        item { Spacer(Modifier.height(14.dp)) }
        items(
            dailies.sortedWith(
                compareBy<DailiesQuery.Daily> { it.isCompleted }
                    .thenByDescending { (it.lastCompletionDate ?: it.createdAt) as String? }
            )
        )
        { Daily(it) }
        item { Spacer(Modifier.height(80.dp)) }
    }
}