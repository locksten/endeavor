package com.example.endeavor.ui.todo.task

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloNetworkException
import com.example.endeavor.*
import com.example.endeavor.ui.theme.Theme
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@Composable
fun Task(task: TasksQuery.Task) {
    var isEditDialogOpen by remember { mutableStateOf(false) }
    Row(
        Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(horizontal = 16.dp)
            .pointerInput(Unit) {
                detectTapGestures(onLongPress = {
                    isEditDialogOpen = true
                })
            },
        Arrangement.Start,
    ) {
        CTaskCheckbox(task)
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
        if (isEditDialogOpen) CUpdateTaskModal(task) { isEditDialogOpen = false }
    }
}

@Composable
private fun CTaskCheckbox(task: TasksQuery.Task) {
    val scope = rememberCoroutineScope()
    val gql = LocalGQLClient.current
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