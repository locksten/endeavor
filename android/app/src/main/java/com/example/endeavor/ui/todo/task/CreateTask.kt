package com.example.endeavor.ui.todo.task

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloNetworkException
import com.example.endeavor.CreateTaskMutation
import com.example.endeavor.LocalGQLClient
import com.example.endeavor.TasksQuery
import com.example.endeavor.type.CreateTaskInput
import com.example.endeavor.ui.MyTextField
import com.example.endeavor.ui.theme.Theme
import com.example.endeavor.ui.todo.TodoDifficultySelector
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@Composable
fun CCreateTaskModal(onDismissRequest: () -> Unit) {
    val scope = rememberCoroutineScope()
    val gql = LocalGQLClient.current
    var title by remember { mutableStateOf("") }
    var difficulty by remember { mutableStateOf(50) }
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
                MyTextField(
                    title, titleFocusRequester, label = "Title",
                    keyboardActions = KeyboardActions(onDone = {
                        scope.launch {
                            createTask(gql, title, difficulty)
                            onDismissRequest()
                        }
                    }),
                ) { title = it }
                TodoDifficultySelector(value = difficulty, onChange = { difficulty = it })
                Button(
                    onClick = {
                        scope.launch {
                            createTask(gql, title, difficulty)
                            onDismissRequest()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add")
                }
            }
        }
    }
}

suspend fun createTask(gql: ApolloClient, title: String, difficulty: Int) {
    try {
        gql.mutate(
            CreateTaskMutation(CreateTaskInput(title, difficulty))
        ).await()
        gql.query(TasksQuery()).await()
    } catch (e: ApolloNetworkException) {
    }
}