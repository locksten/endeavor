package com.example.endeavor.ui.reward

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloNetworkException
import com.example.endeavor.LocalGQLClient
import com.example.endeavor.RewardsQuery
import com.example.endeavor.UpdateRewardMutation
import com.example.endeavor.type.UpdateRewardInput
import com.example.endeavor.ui.MyTextField
import com.example.endeavor.ui.theme.Theme
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@Composable
fun CUpdateRewardModal(reward: Reward, onDismissRequest: () -> Unit) {
    val scope = rememberCoroutineScope()
    val gql = LocalGQLClient.current
    var title by remember { mutableStateOf<String?>(null) }
    var price by remember { mutableStateOf<Int?>(null) }
    val titleFocusRequester = remember { FocusRequester() }
    val priceFocusRequester = remember { FocusRequester() }
    LaunchedEffect(true) {
        titleFocusRequester.requestFocus()
    }

    Dialog(onDismissRequest) {
        Box(
            modifier = Modifier
                .background(Theme.colors.background)
                .fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                MyTextField(
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
                Button(
                    onClick = {
                        scope.launch {
                            updateReward(
                                gql,
                                reward.id,
                                title,
                                price,
                            )
                            onDismissRequest()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save")
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