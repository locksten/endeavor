package com.example.endeavor.ui.todo.daily

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloNetworkException
import com.example.endeavor.DailiesQuery
import com.example.endeavor.DeleteDailyMutation
import com.example.endeavor.LocalGQLClient
import com.example.endeavor.ui.DeleteWithConfirmationButton
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@Composable
fun DeleteDailyButton(task: DailiesQuery.Daily, onDelete: () -> Unit) {
    val scope = rememberCoroutineScope()
    val gql = LocalGQLClient.current
    DeleteWithConfirmationButton {
        scope.launch {
            deleteDaily(gql, task.id)
            onDelete()
        }
    }
}

suspend fun deleteDaily(gql: ApolloClient, id: String) {
    try {
        gql.mutate(DeleteDailyMutation(id)).await()
        gql.query(DailiesQuery()).await()
    } catch (e: ApolloNetworkException) {
    }
}