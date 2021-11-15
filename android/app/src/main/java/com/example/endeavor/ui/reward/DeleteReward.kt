package com.example.endeavor.ui.reward

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloNetworkException
import com.example.endeavor.DeleteRewardMutation
import com.example.endeavor.MutationComposable
import com.example.endeavor.RewardsQuery
import com.example.endeavor.ui.ButtonWithConfirmationDelete
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@Composable
fun CDeleteRewardButton(reward: Reward, onDelete: () -> Unit) {
    MutationComposable { gql, scope ->
        ButtonWithConfirmationDelete {
            scope.launch {
                deleteReward(gql, reward.id)
                onDelete()
            }
        }
    }
}

suspend fun deleteReward(gql: ApolloClient, id: String) {
    try {
        gql.mutate(DeleteRewardMutation(id)).await()
        gql.query(RewardsQuery()).await()
    } catch (e: ApolloNetworkException) {
    }
}