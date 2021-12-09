package com.example.endeavor.ui.todo.daily

import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloNetworkException
import com.example.endeavor.CreateDailyMutation
import com.example.endeavor.DailiesQuery
import com.example.endeavor.MutationComposable
import com.example.endeavor.type.CreateDailyInput
import com.example.endeavor.ui.AddButton
import com.example.endeavor.ui.ColumnDialog
import com.example.endeavor.ui.todo.TodoInput
import com.example.endeavor.ui.todo.TodoInputData
import com.example.endeavor.ui.todo.toCreateTodoInput
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@Composable
fun CCreateDailyModal(onDismissRequest: () -> Unit) {
    MutationComposable { gql, scope ->
        var todoInputData by remember {
            mutableStateOf(TodoInputData.defaultValues())
        }
        fun create() {
            scope.launch {
                createDaily(gql, CreateDailyInput(todoInputData.toCreateTodoInput()))
                onDismissRequest()
            }
        }

        ColumnDialog(onDismissRequest) {
            TodoInput(
                value = todoInputData,
                onChange = { todoInputData = it },
                onDone = { create() })
            AddButton { create() }
        }
    }
}

suspend fun createDaily(gql: ApolloClient, createDailyInput: CreateDailyInput) {
    try {
        gql.mutate(
            CreateDailyMutation(createDailyInput)
        ).await()
        gql.query(DailiesQuery()).await()
    } catch (e: ApolloNetworkException) {
    }
}