package com.example.endeavor.ui.todo.task

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
import com.example.endeavor.TasksQuery
import com.example.endeavor.UpdateTaskMutation
import com.example.endeavor.type.UpdateTaskInput
import com.example.endeavor.ui.theme.Theme
import com.example.endeavor.ui.todo.TodoDifficultySelector
import com.example.endeavor.ui.todo.TodoTitleTextField
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@Composable
fun CUpdateTaskModal(task: TasksQuery.Task, onDismissRequest: () -> Unit) {
    val scope = rememberCoroutineScope()
    var difficulty by remember { mutableStateOf<Int?>(null) }
    val gql = LocalGQLClient.current
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
                TodoTitleTextField(title ?: task.title, titleFocusRequester) { title = it }
                TodoDifficultySelector(
                    value = difficulty ?: task.difficulty,
                    onChange = { difficulty = it })
                Button(
                    onClick = {
                        scope.launch {
                            updateTask(gql, task.id, title, difficulty)
                            onDismissRequest()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save")
                }
            }
        }
    }
}

suspend fun updateTask(gql: ApolloClient, id: String, title: String?, difficulty: Int?) {
    try {
        gql.mutate(
            UpdateTaskMutation(
                UpdateTaskInput(
                    id,
                    title = Input.optional(title),
                    difficulty = Input.optional(difficulty)
                )
            )
        ).await().data?.updateTask
        gql.query(TasksQuery()).await()
    } catch (e: ApolloNetworkException) {
    }
}