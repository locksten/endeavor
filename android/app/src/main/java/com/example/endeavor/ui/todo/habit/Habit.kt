package com.example.endeavor.ui.todo.habit

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloNetworkException
import com.example.endeavor.DoNegativeHabitMutation
import com.example.endeavor.DoPositiveHabitMutation
import com.example.endeavor.HabitsQuery
import com.example.endeavor.LocalGQLClient
import com.example.endeavor.ui.theme.Theme
import kotlinx.coroutines.launch
import kotlin.math.abs

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun Habit(habit: HabitsQuery.Habit) {
    var isUpdateDialogOpen by remember { mutableStateOf(false) }
    Row(
        Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 52.dp)
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(8.dp))
            .background(Theme.colors.graySurface)
            .combinedClickable(
                onClick = {},
                onLongClick = {
                    isUpdateDialogOpen = true
                }
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CHabitButton(isPositive = false, habit = habit)
        Text(
            text = habit.title,
            fontSize = 20.sp,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            textAlign = when {
                isHabitPos(habit) -> TextAlign.End
                isHabitNeg(habit) -> TextAlign.Start
                else -> TextAlign.Center
            }
        )
        CHabitButton(isPositive = true, habit = habit)
        if (isUpdateDialogOpen) CUpdateHabitModal(habit) { isUpdateDialogOpen = false }
    }
}

@Composable
fun CHabitButton(isPositive: Boolean, habit: HabitsQuery.Habit) {
    val scope = rememberCoroutineScope()
    val gql = LocalGQLClient.current

    val count = if (isPositive) {
        habit.positiveCount
    } else {
        habit.negativeCount
    }?.let { abs(it) }

    val sign = if (isPositive) {
        "+"
    } else {
        "-"
    }

    if (count != null) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .width(56.dp)
                .fillMaxHeight()
                .clickable(
                    onClick = {
                        scope.launch {
                            if (isPositive) {
                                doPositiveHabit(gql, habit.id)
                            } else {
                                doNegativeHabit(gql, habit.id)
                            }
                        }
                    },
                )
                .background(
                    if (isPositive) {
                        Theme.colors.positiveHabitButton
                    } else {
                        Theme.colors.negativeHabitButton
                    }
                )
        )
        {
            Text(
                text = "${sign}${count}",
                fontWeight = FontWeight.Bold,
                color = if (isPositive) {
                    Theme.colors.onPositiveHabitButton
                } else {
                    Theme.colors.onNegativeHabitButton
                }
            )
        }
    }

}

suspend fun doPositiveHabit(gql: ApolloClient, id: String) {
    try {
        gql.mutate(
            DoPositiveHabitMutation(id)
        ).await()
        gql.query(HabitsQuery()).await()
    } catch (e: ApolloNetworkException) {
    }
}

suspend fun doNegativeHabit(gql: ApolloClient, id: String) {
    try {
        gql.mutate(
            DoNegativeHabitMutation(id)
        ).await()
        gql.query(HabitsQuery()).await()
    } catch (e: ApolloNetworkException) {
    }
}

fun isHabitPosNeg(a: HabitsQuery.Habit): Boolean {
    return a.negativeCount != null && a.positiveCount != null
}

fun isHabitPos(a: HabitsQuery.Habit): Boolean {
    return a.negativeCount == null && a.positiveCount != null
}

fun isHabitNeg(a: HabitsQuery.Habit): Boolean {
    return a.negativeCount != null && a.positiveCount == null
}