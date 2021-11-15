package com.example.endeavor.ui.todo.daily

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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloNetworkException
import com.example.endeavor.DailiesQuery
import com.example.endeavor.LocalGQLClient
import com.example.endeavor.UpdateDailyMutation
import com.example.endeavor.type.UpdateDailyInput
import com.example.endeavor.ui.MyTextField
import com.example.endeavor.ui.theme.Theme
import com.example.endeavor.ui.todo.TodoDifficultySelector
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@Composable
fun CUpdateDailyModal(daily: DailiesQuery.Daily, onDismissRequest: () -> Unit) {
    val scope = rememberCoroutineScope()
    val gql = LocalGQLClient.current
    var difficulty by remember { mutableStateOf<Int?>(null) }
    var title by remember { mutableStateOf<String?>(null) }
    val titleFocusRequester = remember { FocusRequester() }
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
                    title ?: daily.title, titleFocusRequester, label = "Title",
                    keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Words),
                    keyboardActions = KeyboardActions(onDone = {
                        scope.launch {
                            updateDaily(gql, daily.id, title, difficulty)
                            onDismissRequest()
                        }
                    }),
                ) { title = it }
                TodoDifficultySelector(
                    value = difficulty ?: daily.difficulty,
                    onChange = { difficulty = it })
                CDeleteDailyButton(daily, onDismissRequest)
                Button(
                    onClick = {
                        scope.launch {
                            updateDaily(gql, daily.id, title, difficulty)
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

suspend fun updateDaily(gql: ApolloClient, id: String, title: String?, difficulty: Int?) {
    try {
        gql.mutate(
            UpdateDailyMutation(
                UpdateDailyInput(
                    id,
                    title = Input.optional(title),
                    difficulty = Input.optional(difficulty)
                )
            )
        ).await()
        gql.query(DailiesQuery()).await()
    } catch (e: ApolloNetworkException) {
    }
}