package com.example.endeavor.ui.todo.habit

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloNetworkException
import com.example.endeavor.LocalGQLClient
import com.example.endeavor.HabitsQuery
import com.example.endeavor.UpdateHabitMutation
import com.example.endeavor.type.UpdateHabitInput
import com.example.endeavor.ui.theme.Theme
import com.example.endeavor.ui.todo.TodoDifficultySelector
import com.example.endeavor.ui.todo.TodoTitleTextField
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@Composable
fun CUpdateHabitModal(habit: HabitsQuery.Habit, onDismissRequest: () -> Unit) {
    val scope = rememberCoroutineScope()
    val gql = LocalGQLClient.current
    var difficulty by remember { mutableStateOf<Int?>(null) }
    var positiveCount by remember { mutableStateOf<Boolean?>(null) }
    var negativeCount by remember { mutableStateOf<Boolean?>(null) }
    var title by remember { mutableStateOf<String?>(null) }
    val titleFocusRequester = remember { FocusRequester() }
    LaunchedEffect(true) {
        titleFocusRequester.requestFocus()
    }

    Dialog(onDismissRequest) {
        Box(
            modifier = Modifier
                .background(Theme.colors.background)
                .fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                TodoTitleTextField(title ?: habit.title, titleFocusRequester) { title = it }
                TodoDifficultySelector(
                    value = difficulty ?: habit.difficulty,
                    onChange = { difficulty = it })
                HabitTypeSelector(
                    positiveValue = positiveCount ?: (habit.positiveCount != null),
                    negativeValue = negativeCount ?: (habit.negativeCount != null),
                    onChangePositive = { positiveCount = it },
                    onChangeNegative = { negativeCount = it })
                Button(
                    onClick = {
                        scope.launch {
                            updateHabit(
                                gql,
                                habit.id,
                                title,
                                difficulty,
                                positiveCount,
                                negativeCount,
                            )
                            onDismissRequest()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save")
                }
                DeleteHabitButton(habit, onDismissRequest)
            }
        }
    }
}

suspend fun updateHabit(
    gql: ApolloClient,
    id: String,
    title: String?,
    difficulty: Int?,
    positiveCount: Boolean?,
    negativeCount: Boolean?
) {
    try {
        gql.mutate(
            UpdateHabitMutation(
                UpdateHabitInput(
                    id,
                    title = Input.optional(title),
                    difficulty = Input.optional(difficulty),
                    positiveCount = Input.optional(positiveCount),
                    negativeCount = Input.optional(negativeCount),
                )
            )
        ).await()
        gql.query(HabitsQuery()).await()
    } catch (e: ApolloNetworkException) {
    }
}