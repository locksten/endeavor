package com.example.endeavor.ui.todo.habit

import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloNetworkException
import com.example.endeavor.HabitsQuery
import com.example.endeavor.MutationComposable
import com.example.endeavor.UpdateHabitMutation
import com.example.endeavor.type.UpdateHabitInput
import com.example.endeavor.ui.CDeleteTodoButton
import com.example.endeavor.ui.ColumnDialog
import com.example.endeavor.ui.SaveButton
import com.example.endeavor.ui.todo.TodoInput
import com.example.endeavor.ui.todo.TodoInputData
import com.example.endeavor.ui.todo.toUpdateTodoInput
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@Composable
fun CUpdateHabitModal(habit: HabitsQuery.Habit, onDismissRequest: () -> Unit) {
    MutationComposable { gql, scope ->
        var todoInputData by remember {
            mutableStateOf(TodoInputData())
        }
        var positiveCount by remember { mutableStateOf<Boolean?>(null) }
        var negativeCount by remember { mutableStateOf<Boolean?>(null) }
        fun update() {
            scope.launch {
                updateHabit(
                    gql,
                    UpdateHabitInput(
                        id = habit.id,
                        positiveCount = Input.optional(positiveCount),
                        negativeCount = Input.optional(negativeCount),
                        updateTodoInput = todoInputData.toUpdateTodoInput()
                    )
                )
                onDismissRequest()
            }
        }

        ColumnDialog(onDismissRequest) {
            TodoInput(
                defaultValue = TodoInputData(
                    title = habit.title,
                    difficulty = habit.difficulty
                ),
                value = todoInputData,
                onChange = { todoInputData = it },
                onDone = { update() })
            HabitTypeSelector(
                positiveValue = positiveCount ?: (habit.positiveCount != null),
                negativeValue = negativeCount ?: (habit.negativeCount != null),
                onChangePositive = { positiveCount = it },
                onChangeNegative = { negativeCount = it })
            CDeleteTodoButton(habit.id, onDismissRequest) { gql ->
                gql.query(HabitsQuery()).await()
            }
            SaveButton { update() }
        }
    }
}

suspend fun updateHabit(
    gql: ApolloClient,
    updateHabitInput: UpdateHabitInput
) {
    try {
        gql.mutate(
            UpdateHabitMutation(updateHabitInput)
        ).await()
        gql.query(HabitsQuery()).await()
    } catch (e: ApolloNetworkException) {
    }
}