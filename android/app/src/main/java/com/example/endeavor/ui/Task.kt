package com.example.endeavor.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.TextFieldDefaults.textFieldColors
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloNetworkException
import com.example.endeavor.*
import com.example.endeavor.ui.theme.EndeavorTheme
import com.example.endeavor.ui.theme.Theme
import kotlinx.coroutines.launch

@Preview(
    name = "Light Mode",
    showBackground = true,
    device = Devices.PIXEL_2
)
@Preview(
    name = "Dark Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    device = Devices.PIXEL_2
)
@Composable
fun TasksPreview() {
    EndeavorTheme {
        TaskList(testTasks)
    }
}

val testTasks = listOf(
    TasksQuery.Task(
        title = "Title A",
        isCompleted = false,
        difficulty = 8,
        createdAt = "",
        id = "",
        __typename = "Task"
    ),
    TasksQuery.Task(
        title = "Title B",
        isCompleted = false,
        difficulty = 8,
        createdAt = "",
        id = "",
        __typename = "Task"
    ),
    TasksQuery.Task(
        title = "Title C",
        isCompleted = true,
        difficulty = 8,
        createdAt = "",
        id = "",
        __typename = "Task"
    ),
    TasksQuery.Task(
        title = "Title D",
        isCompleted = false,
        difficulty = 8,
        createdAt = "",
        id = "",
        __typename = "Task"
    ),
)

@Composable
fun Task(task: TasksQuery.Task) {
    val scope = rememberCoroutineScope()
    val gql = LocalGQLClient.current
    Row(
        Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(horizontal = 16.dp),
        Arrangement.Start,
    ) {
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = {
                if (it) {
                    scope.launch {
                        completeTask(gql, task.id)
                    }
                }
            },
            modifier = Modifier.fillMaxHeight(),
            colors = CheckboxDefaults.colors(
                uncheckedColor = Theme.colors.onBackground,
                checkmarkColor = Theme.colors.onBackground,
                checkedColor = Color.Transparent
            )
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = task.title,
            textDecoration = if (task.isCompleted) {
                TextDecoration.LineThrough
            } else {
                null
            },
            fontSize = 25.sp,
            modifier = Modifier.alpha(
                if (task.isCompleted) 0.5f else 1f,
            )
        )
    }
}

@Composable
fun TaskList(tasks: List<TasksQuery.Task>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(tasks.sortedByDescending { it.id }) { index, task ->
            if (index == 0) Spacer(Modifier.height(12.dp))
            Task(task)
            if (index == tasks.size - 1) Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
fun CTaskList() {
    gqlWatchQuery(TasksQuery())?.me?.tasks?.let { TaskList(it) }
}

suspend fun completeTask(gql: ApolloClient, id: String) {
    try {
        gql.mutate(
            CompleteTaskMutation(completeTaskId = id)
        ).await().data?.completeTask
        gql.query(TasksQuery()).await()
    } catch (e: ApolloNetworkException) {
    }
}

@ExperimentalComposeUiApi
@Composable
fun CCreateTaskModal(onDismissRequest: () -> Unit) {
    val scope = rememberCoroutineScope()
    val gql = LocalGQLClient.current
    var title by remember { mutableStateOf("") }
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
                TaskTitleTexField(title, titleFocusRequester) { title = it }
                Button(
                    onClick = {
                        scope.launch {
                            createTask(gql, title, 3)
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

@Composable
private fun TaskTitleTexField(
    title: String,
    focusRequester: FocusRequester,
    onChange: (String) -> Unit
) {
    TextField(
        value = title,
        onValueChange = onChange,
        label = { Text("Title") },
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        colors = textFieldColors(textColor = Theme.colors.onBackground)
    )
}


suspend fun createTask(gql: ApolloClient, title: String, difficulty: Int) {
    try {
        gql.mutate(
            CreateTaskMutation(
                createTaskTitle = title,
                createTaskDifficulty = difficulty
            )
        ).await().data?.createTask
        gql.query(TasksQuery()).await()
    } catch (e: ApolloNetworkException) {
    }
}