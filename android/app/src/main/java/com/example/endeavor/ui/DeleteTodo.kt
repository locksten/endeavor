package com.example.endeavor.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloNetworkException
import com.example.endeavor.DeleteTodoMutation
import com.example.endeavor.MutationComposable
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@Composable
fun CDeleteTodoButton(id: String, onDelete: () -> Unit, reQuery: suspend (gql: ApolloClient) -> Unit) {
    MutationComposable { gql, scope ->
        ButtonWithConfirmationDelete {
            scope.launch {
                try {
                    gql.mutate(DeleteTodoMutation(id)).await()
                    reQuery(gql)
                } catch (e: ApolloNetworkException) {
                }
                onDelete()
            }
        }
    }
}