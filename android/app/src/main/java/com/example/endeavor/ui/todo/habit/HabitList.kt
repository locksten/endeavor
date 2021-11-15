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
    val grouped = habits.sortedBy { it.createdAt as String }.groupBy {
        when {
            isHabitPos(it) -> 0
            isHabitPosNeg(it) -> 1
            isHabitNeg(it) -> 2
            else -> 3
        }
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        item { Spacer(Modifier.height(14.dp)) }
        grouped[0]?.let { habits -> items(habits) { Habit(it) } }
        grouped[1]?.let { habits -> items(habits) { Habit(it) } }
        grouped[2]?.let { habits -> items(habits) { Habit(it) } }
        grouped[3]?.let { habits -> items(habits) { Habit(it) } }
        item { Spacer(Modifier.height(80.dp)) }
    }
}