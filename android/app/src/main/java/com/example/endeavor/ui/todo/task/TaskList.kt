package com.example.endeavor.ui.todo.task

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.endeavor.TasksQuery
import com.example.endeavor.gqlWatchQuery
import com.example.endeavor.ui.theme.EndeavorTheme

@ExperimentalComposeUiApi
@Composable
fun CTaskList() {
    gqlWatchQuery(TasksQuery())?.me?.tasks?.let { TaskList(it) }
}

@ExperimentalComposeUiApi
@Composable
fun TaskList(tasks: List<TasksQuery.Task>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item { Spacer(Modifier.height(0.dp)) }
        items(tasks.sortedByDescending { it.id }) { Task(it) }
        item { Spacer(Modifier.height(0.dp)) }
    }
}

@ExperimentalComposeUiApi
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