package com.example.endeavor.ui.todo.task

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.unit.dp
import com.example.endeavor.TasksQuery
import com.example.endeavor.gqlWatchQuery
import com.example.endeavor.ui.AppLazyColumn

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@Composable
fun CTaskList() {
    gqlWatchQuery(TasksQuery())?.me?.tasks?.let { TaskList(it) }
}

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@Composable
fun TaskList(tasks: List<TasksQuery.Task>) {
    AppLazyColumn(fabPadding = true, spacedBy = 0.dp) {
        items(
            tasks.sortedWith(
                compareBy<TasksQuery.Task> { it.isCompleted }
                    .thenByDescending { (it.completionDate ?: it.createdAt) as String? }
            ), { it.id }
        ) { Task(it) }
    }
}