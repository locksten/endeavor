package com.example.endeavor.ui.todo.task

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.endeavor.TasksQuery
import com.example.endeavor.gqlWatchQuery

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
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        item { Spacer(Modifier.height(14.dp)) }
        items(
            tasks.sortedWith(
                compareBy<TasksQuery.Task> { it.isCompleted }
                    .thenByDescending { (it.completionDate ?: it.createdAt) as String? }
            )
        ) { Task(it) }
        item { Spacer(Modifier.height(80.dp)) }
    }
}