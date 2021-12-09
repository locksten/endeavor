package com.example.endeavor.ui.todo.task

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloNetworkException
import com.example.endeavor.CreateTaskMutation
import com.example.endeavor.MutationComposable
import com.example.endeavor.TasksQuery
import com.example.endeavor.type.CreateTaskInput
import com.example.endeavor.ui.AddButton
import com.example.endeavor.ui.ColumnDialog
import com.example.endeavor.ui.todo.TodoInput
import com.example.endeavor.ui.todo.TodoInputData
import com.example.endeavor.ui.todo.toCreateTodoInput
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@Composable
fun CCreateTaskModal(onDismissRequest: () -> Unit) {
    MutationComposable { gql, scope ->
        var todoInputData by remember {
            mutableStateOf(TodoInputData.defaultValues())
        }
        fun create() {
            scope.launch {
                createTask(gql, CreateTaskInput(todoInputData.toCreateTodoInput()))
                onDismissRequest()
            }
        }

        ColumnDialog(onDismissRequest) {
            TodoInput(
                value = todoInputData,
                onChange = { todoInputData = it },
                onDone = { create() },
                keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Sentences)
            )
            AddButton { create() }
        }
    }
}

suspend fun createTask(gql: ApolloClient, createTaskInput: CreateTaskInput) {
    try {
        gql.mutate(
            CreateTaskMutation(createTaskInput)
        ).await()
        gql.query(TasksQuery()).await()
    } catch (e: ApolloNetworkException) {
    }
}