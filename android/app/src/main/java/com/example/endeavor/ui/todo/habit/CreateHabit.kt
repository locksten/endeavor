package com.example.endeavor.ui.todo.habit

import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloNetworkException
import com.example.endeavor.CreateHabitMutation
import com.example.endeavor.HabitsQuery
import com.example.endeavor.MutationComposable
import com.example.endeavor.type.CreateHabitInput
import com.example.endeavor.ui.AddButton
import com.example.endeavor.ui.ColumnDialog
import com.example.endeavor.ui.todo.TodoInput
import com.example.endeavor.ui.todo.TodoInputData
import com.example.endeavor.ui.todo.toCreateTodoInput
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@Composable
fun CCreateHabitModal(onDismissRequest: () -> Unit) {
    MutationComposable { gql, scope ->
        var todoInputData by remember {
            mutableStateOf(TodoInputData.defaultValues())
        }
        var positiveCount by remember { mutableStateOf(true) }
        var negativeCount by remember { mutableStateOf(true) }
        fun create() {
            scope.launch {
                createHabit(
                    gql,
                    CreateHabitInput(
                        positiveCount = positiveCount,
                        negativeCount = negativeCount,
                        createTodoInput = todoInputData.toCreateTodoInput()
                    )
                )
                onDismissRequest()
            }
        }

        ColumnDialog(onDismissRequest) {
            TodoInput(
                value = todoInputData,
                onChange = { todoInputData = it },
                onDone = { create() })
            HabitTypeSelector(
                negativeValue = negativeCount,
                positiveValue = positiveCount,
                onChangePositive = { positiveCount = it },
                onChangeNegative = { negativeCount = it })
            AddButton { create() }
        }
    }
}

suspend fun createHabit(
    gql: ApolloClient, createHabitInput: CreateHabitInput
) {
    try {
        gql.mutate(
            CreateHabitMutation(createHabitInput)
        ).await()
        gql.query(HabitsQuery()).await()
    } catch (e: ApolloNetworkException) {
    }
}