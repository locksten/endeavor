package com.example.endeavor.ui.todo.habit

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import com.example.endeavor.HabitsQuery
import com.example.endeavor.gqlWatchQuery
import com.example.endeavor.ui.AppLazyColumn
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
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
    val grouped = habits.sortedWith(compareBy({ it.createdAt }, { it.id })).groupBy {
        when {
            isHabitPos(it) -> 0
            isHabitPosNeg(it) -> 1
            isHabitNeg(it) -> 2
            else -> 3
        }
    }
    AppLazyColumn(fabPadding = true) {
        grouped[0]?.let { habits -> items(habits, { it.id }) { Habit(it) } }
        grouped[1]?.let { habits -> items(habits, { it.id }) { Habit(it) } }
        grouped[2]?.let { habits -> items(habits, { it.id }) { Habit(it) } }
        grouped[3]?.let { habits -> items(habits, { it.id }) { Habit(it) } }
    }
}