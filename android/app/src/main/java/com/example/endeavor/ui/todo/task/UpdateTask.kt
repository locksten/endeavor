package com.example.endeavor.ui.todo.task

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloNetworkException
import com.example.endeavor.MutationComposable
import com.example.endeavor.TasksQuery
import com.example.endeavor.UpdateTaskMutation
import com.example.endeavor.type.UpdateTaskInput
import com.example.endeavor.ui.CDeleteTodoButton
import com.example.endeavor.ui.ColumnDialog
import com.example.endeavor.ui.SaveButton
import com.example.endeavor.ui.todo.TodoInput
import com.example.endeavor.ui.todo.TodoInputData
import com.example.endeavor.ui.todo.toUpdateTodoInput
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@Composable
fun CUpdateTaskModal(task: TasksQuery.Task, onDismissRequest: () -> Unit) {
    MutationComposable { gql, scope ->
        var todoInputData by remember { mutableStateOf(TodoInputData()) }
        fun update() {
            scope.launch {
                updateTask(
                    gql,
                    UpdateTaskInput(task.id, todoInputData.toUpdateTodoInput())
                )
                onDismissRequest()
            }
        }

        ColumnDialog(onDismissRequest) {
            TodoInput(
                defaultValue = TodoInputData(
                    title = task.title,
                    difficulty = task.difficulty
                ),
                value = todoInputData,
                onChange = { todoInputData = it },
                onDone = { update() },
                keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Sentences)
            )
            CDeleteTodoButton(task.id, onDismissRequest) { gql ->
                gql.query(TasksQuery()).await()
            }
            SaveButton { update() }
        }
    }
}

suspend fun updateTask(gql: ApolloClient, updateTaskInput: UpdateTaskInput) {
    try {
        gql.mutate(
            UpdateTaskMutation(
                updateTaskInput
            )
        ).await()
        gql.query(TasksQuery()).await()
    } catch (e: ApolloNetworkException) {
    }
}