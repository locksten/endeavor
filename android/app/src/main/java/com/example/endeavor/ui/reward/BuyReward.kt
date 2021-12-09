package com.example.endeavor.ui.reward

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloNetworkException
import com.example.endeavor.BuyRewardMutation
import com.example.endeavor.MutationComposable
import com.example.endeavor.RewardsQuery
import com.example.endeavor.ui.ColumnDialog
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@Composable
fun CBuyRewardModal(reward: Reward, gold: Int, onDismissRequest: () -> Unit) {
    MutationComposable { gql, scope ->
        ColumnDialog(onDismissRequest) {
            Text(
                text = reward.title,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 32.dp)
            )
            Button(
                onClick = {
                    scope.launch {
                        buyReward(gql, reward.id)
                        onDismissRequest()
                    }
                },
                enabled = reward.price <= gold,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row {
                    Text(
                        text = "Buy for",
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "💰${reward.price}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

        }
    }
}

suspend fun buyReward(gql: ApolloClient, id: String) {
    try {
        gql.mutate(BuyRewardMutation(id)).await()
        gql.query(RewardsQuery()).await()
    } catch (e: ApolloNetworkException) {
    }
}