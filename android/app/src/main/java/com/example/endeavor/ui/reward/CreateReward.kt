package com.example.endeavor.ui.reward

import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.input.KeyboardType
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloNetworkException
import com.example.endeavor.CreateRewardMutation
import com.example.endeavor.MutationComposable
import com.example.endeavor.RewardsQuery
import com.example.endeavor.type.CreateRewardInput
import com.example.endeavor.ui.AppTextField
import com.example.endeavor.ui.ColumnDialog
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@Composable
fun CCreateRewardModal(onDismissRequest: () -> Unit) {
    MutationComposable { gql, scope ->
        var title by remember { mutableStateOf("") }
        var price by remember { mutableStateOf<Int?>(null) }
        val titleFocusRequester = remember { FocusRequester() }
        val priceFocusRequester = remember { FocusRequester() }
        LaunchedEffect(true) {
            titleFocusRequester.requestFocus()
        }

        ColumnDialog(onDismissRequest) {
            AppTextField(
                label = "Title",
                value = title,
                onChange = { title = it },
                focusRequester = titleFocusRequester,
                keyboardActions = KeyboardActions(onNext = { priceFocusRequester.requestFocus() }),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    capitalization = KeyboardCapitalization.Words
                ),
            )
            PriceTextField(
                value = price,
                onValueChange = { price = it },
                focusRequester = priceFocusRequester,
                onDone = {
                    scope.launch {
                        createReward(gql, title, price ?: 1)
                        onDismissRequest()
                    }
                }
            )
            Button(
                onClick = {
                    scope.launch {
                        createReward(gql, title, price ?: 1)
                        onDismissRequest()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create")
            }

        }
    }
}

@Composable
fun PriceTextField(
    value: Int?,
    onValueChange: (Int?) -> Unit,
    onDone: () -> Unit,
    focusRequester: FocusRequester
) {
    var isError by remember { mutableStateOf(false) }
    AppTextField(
        label = "Price",
        value = value?.toString() ?: "",
        onChange = {
            isError = false
            onValueChange(
                try {
                    it.toInt()
                } catch (e: NumberFormatException) {
                    isError = true
                    null
                }
            )
        },
        isError = isError,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Number
        ),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        focusRequester = focusRequester
    )
}

suspend fun createReward(gql: ApolloClient, title: String, price: Int) {
    try {
        gql.mutate(
            CreateRewardMutation(CreateRewardInput(title, price))
        ).await()
        gql.query(RewardsQuery()).await()
    } catch (e: ApolloNetworkException) {
    }
}