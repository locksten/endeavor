package com.example.endeavor.ui.todo.habit

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.endeavor.HabitsQuery
import com.example.endeavor.gqlWatchQuery

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@Composable
fun CHabitList() {
    gqlWatchQuery(HabitsQuery())?.me?.habits?.let { HabitList(it) }
}

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@Composable
fun HabitList(habits: List<HabitsQuery.Habit>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp)
    ) {
        item { Spacer(Modifier.height(14.dp)) }
        items(
            habits.sortedWith(
                compareBy { it.id }
            )
        ) { Habit(it) }
        item { Spacer(Modifier.height(14.dp)) }
    }
}