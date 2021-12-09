package com.example.endeavor.ui.todo.daily

import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloNetworkException
import com.example.endeavor.DailiesQuery
import com.example.endeavor.MutationComposable
import com.example.endeavor.UpdateDailyMutation
import com.example.endeavor.type.UpdateDailyInput
import com.example.endeavor.ui.SaveButton
import com.example.endeavor.ui.ColumnDialog
import com.example.endeavor.ui.todo.TodoInput
import com.example.endeavor.ui.todo.TodoInputData
import com.example.endeavor.ui.CDeleteTodoButton
import com.example.endeavor.ui.todo.toUpdateTodoInput
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@Composable
fun CUpdateDailyModal(daily: DailiesQuery.Daily, onDismissRequest: () -> Unit) {
    MutationComposable { gql, scope ->
        var todoInputData by remember { mutableStateOf(TodoInputData()) }
        fun update() {
            scope.launch {
                updateDaily(gql, UpdateDailyInput(daily.id, todoInputData.toUpdateTodoInput()))
                onDismissRequest()
            }
        }

        ColumnDialog(onDismissRequest) {
            TodoInput(
                defaultValue = TodoInputData(
                    title = daily.title,
                    difficulty = daily.difficulty
                ),
                value = todoInputData,
                onChange = { todoInputData = it },
                onDone = { update() })
            CDeleteTodoButton(daily.id, onDismissRequest) { gql ->
                gql.query(DailiesQuery()).await()
            }
            SaveButton { update() }
        }
    }
}

suspend fun updateDaily(gql: ApolloClient, updateDailyInput: UpdateDailyInput) {
    try {
        gql.mutate(
            UpdateDailyMutation(updateDailyInput)
        ).await()
        gql.query(DailiesQuery()).await()
    } catch (e: ApolloNetworkException) {
    }
}