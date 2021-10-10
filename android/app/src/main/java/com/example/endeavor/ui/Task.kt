package com.example.endeavor.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            color = if (task.isCompleted) {
                Theme.rawColors.gray400
            } else {
                Theme.colors.onBackground
            },
            textDecoration = if (task.isCompleted) {
                TextDecoration.LineThrough
            } else {
                null
            },
            fontSize = 25.sp
        )
    }
}

@Composable
fun TaskList(tasks: List<TasksQuery.Task>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(tasks.sortedBy { it.id }) { index, task ->
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

suspend fun createTask(gql: ApolloClient) {
    try {
        gql.mutate(
            CreateTaskMutation(
                createTaskTitle = "new Title",
                createTaskDifficulty = 9
            )
        ).await().data?.createTask
        gql.query(TasksQuery()).await()
    } catch (e: ApolloNetworkException) {
    }
}