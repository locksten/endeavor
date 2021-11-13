package com.example.endeavor.ui.todo.task

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloNetworkException
import com.example.endeavor.CompleteTaskMutation
import com.example.endeavor.LocalGQLClient
import com.example.endeavor.TasksQuery
import com.example.endeavor.ui.theme.Theme
import kotlinx.coroutines.launch

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@Composable
fun Task(task: TasksQuery.Task) {
    var isUpdateDialogOpen by remember { mutableStateOf(false) }

    Row(
        Modifier
            .fillMaxWidth()

            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(8.dp))
            .combinedClickable(
                onClick = {},
                onLongClick = {
                    isUpdateDialogOpen = true
                }
            )
            .padding(8.dp),
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
            fontSize = 20.sp,
            modifier = Modifier.alpha(
                if (task.isCompleted) 0.5f else 1f,
            )
        )
        if (isUpdateDialogOpen) CUpdateTaskModal(task) { isUpdateDialogOpen = false }
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
        modifier = Modifier
            .fillMaxHeight(),
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
            CompleteTaskMutation(id)
        ).await()
        gql.query(TasksQuery()).await()
    } catch (e: ApolloNetworkException) {
    }
}