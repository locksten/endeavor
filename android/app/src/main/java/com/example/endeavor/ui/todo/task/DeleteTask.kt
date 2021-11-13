package com.example.endeavor.ui.todo.task

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloNetworkException
import com.example.endeavor.DeleteTaskMutation
import com.example.endeavor.LocalGQLClient
import com.example.endeavor.TasksQuery
import com.example.endeavor.ui.DeleteWithConfirmationButton
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@Composable
fun DeleteTaskButton(task: TasksQuery.Task, onDelete: () -> Unit) {
    val scope = rememberCoroutineScope()
    val gql = LocalGQLClient.current
    DeleteWithConfirmationButton {
        scope.launch {
            deleteTask(gql, task.id)
            onDelete()
        }
    }
}

suspend fun deleteTask(gql: ApolloClient, id: String) {
    try {
        gql.mutate(DeleteTaskMutation(id)).await()
        gql.query(TasksQuery()).await()
    } catch (e: ApolloNetworkException) {
    }
}