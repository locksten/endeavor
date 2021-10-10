package com.example.endeavor.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.endeavor.TasksQuery
import com.example.endeavor.gqlWatchQuery
import com.example.endeavor.ui.theme.EndeavorTheme
import com.example.endeavor.ui.theme.Theme

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
    Row(
        Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(horizontal = 16.dp),
        Arrangement.Start,
    ) {
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = {},
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
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 12.dp)
    ) {
        items(tasks) {
            Task(it)
        }
    }
}

@Composable
fun CTaskList() {
    gqlWatchQuery(TasksQuery())?.me?.tasks?.let { TaskList(it) }
}