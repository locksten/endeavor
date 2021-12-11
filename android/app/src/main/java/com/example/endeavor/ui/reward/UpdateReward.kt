package com.example.endeavor.ui.reward

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloNetworkException
import com.example.endeavor.MutationComposable
import com.example.endeavor.RewardsQuery
import com.example.endeavor.UpdateRewardMutation
import com.example.endeavor.type.UpdateRewardInput
import com.example.endeavor.ui.AppTextField
import com.example.endeavor.ui.ColumnDialog
import com.example.endeavor.ui.SaveButton
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@Composable
fun CUpdateRewardModal(reward: Reward, onDismissRequest: () -> Unit) {
    MutationComposable { gql, scope ->
        var title by remember { mutableStateOf<String?>(null) }
        var price by remember { mutableStateOf<Int?>(null) }
        val titleFocusRequester = remember { FocusRequester() }
        val priceFocusRequester = remember { FocusRequester() }
        LaunchedEffect(true) {
            titleFocusRequester.requestFocus()
        }

        ColumnDialog(onDismissRequest) {
            AppTextField(
                label = "Title",
                value = title ?: reward.title,
                onChange = { title = it },
                focusRequester = titleFocusRequester,
                keyboardActions = KeyboardActions(onNext = { priceFocusRequester.requestFocus() }),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    capitalization = KeyboardCapitalization.Words
                ),
            )
            PriceTextField(
                value = price ?: reward.price,
                onValueChange = { price = it },
                focusRequester = priceFocusRequester,
                onDone = {
                    scope.launch {
                        updateReward(gql, reward.id, title, price)
                        onDismissRequest()
                    }
                }
            )
            CDeleteRewardButton(reward, onDismissRequest)
            SaveButton {
                scope.launch {
                    updateReward(
                        gql,
                        reward.id,
                        title,
                        price,
                    )
                    onDismissRequest()
                }
            }
        }
    }
}

suspend fun updateReward(
    gql: ApolloClient,
    id: String,
    title: String?,
    price: Int?,
) {
    try {
        gql.mutate(
            UpdateRewardMutation(
                UpdateRewardInput(
                    id,
                    title = Input.optional(title),
                    price = Input.optional(price),
                )
            )
        ).await()
        gql.query(RewardsQuery()).await()
    } catch (e: ApolloNetworkException) {
    }
}