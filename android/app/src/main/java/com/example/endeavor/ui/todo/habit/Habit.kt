package com.example.endeavor.ui.todo.habit

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
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
        Arrangement.Start,
    ) {
        HabitButton(isPositive = false, habit = habit)
        Spacer(Modifier.width(16.dp))
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            Text(
                text = habit.title,
                fontSize = 20.sp,
            )
        }
        HabitButton(isPositive = true, habit = habit)
        if (isUpdateDialogOpen) CUpdateHabitModal(habit) { isUpdateDialogOpen = false }
    }
}

@Composable
fun HabitButton(isPositive: Boolean, habit: HabitsQuery.Habit) {
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

    Box(
        Modifier
            .width(64.dp)
            .fillMaxHeight()) {
        if (count != null) {
            Button(
                onClick = {
                    scope.launch {
                        if (isPositive) {
                            doPositiveHabit(gql, habit.id)
                        } else {
                            doNegativeHabit(gql, habit.id)
                        }
                    }
                },
                modifier = Modifier.fillMaxHeight(),
                shape = RoundedCornerShape(0),
                colors = if (isPositive) ButtonDefaults.buttonColors(
                    backgroundColor = Theme.colors.positiveHabitButton,
                    contentColor = Theme.colors.onPositiveHabitButton
                ) else {
                    ButtonDefaults.buttonColors(
                        backgroundColor = Theme.colors.negativeHabitButton,
                        contentColor = Theme.colors.onNegativeHabitButton
                    )
                }
            ) {
                Text(
                    "${sign}${count}",
                    fontWeight = FontWeight.Bold
                )
            }
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