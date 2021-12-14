package com.example.endeavor.ui.todo.task

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloNetworkException
import com.example.endeavor.CreateTaskMutation
import com.example.endeavor.MutationComposable
import com.example.endeavor.TasksQuery
import com.example.endeavor.type.CreateTaskInput
import com.example.endeavor.ui.AddButton
import com.example.endeavor.ui.ColumnDialog
import com.example.endeavor.ui.DateTimeInput
import com.example.endeavor.ui.theme.Theme
import com.example.endeavor.ui.todo.TodoInput
import com.example.endeavor.ui.todo.TodoInputData
import com.example.endeavor.ui.todo.toCreateTodoInput
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.DatePickerDefaults
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.launch
import java.time.*

@ExperimentalComposeUiApi
@Composable
fun CCreateTaskModal(onDismissRequest: () -> Unit) {
    MutationComposable { gql, scope ->
        var todoInputData by remember {
            mutableStateOf(TodoInputData.defaultValues())
        }
        var dateTime: OffsetDateTime? by remember { mutableStateOf(null) }

        fun create() {
            scope.launch {
                createTask(
                    gql,
                    CreateTaskInput(Input.fromNullable(dateTime), todoInputData.toCreateTodoInput())
                )
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
            DateTimeInput("Reminder", dateTime) { dateTime = it }
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