package com.example.endeavor.ui.todo.habit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloNetworkException
import com.example.endeavor.CreateHabitMutation
import com.example.endeavor.LocalGQLClient
import com.example.endeavor.HabitsQuery
import com.example.endeavor.type.CreateHabitInput
import com.example.endeavor.ui.theme.Theme
import com.example.endeavor.ui.todo.TodoDifficultySelector
import com.example.endeavor.ui.todo.TodoTitleTextField
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@Composable
fun CCreateHabitModal(onDismissRequest: () -> Unit) {
    val scope = rememberCoroutineScope()
    val gql = LocalGQLClient.current
    var title by remember { mutableStateOf("") }
    var difficulty by remember { mutableStateOf(50) }
    var positiveCount by remember { mutableStateOf(true) }
    var negativeCount by remember { mutableStateOf(true) }
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
                TodoTitleTextField(title, titleFocusRequester) { title = it }
                TodoDifficultySelector(value = difficulty, onChange = { difficulty = it })
                HabitTypeSelector(
                    negativeValue = negativeCount,
                    positiveValue = positiveCount,
                    onChangePositive = { positiveCount = it },
                    onChangeNegative = { negativeCount = it })
                Button(
                    onClick = {
                        scope.launch {
                            createHabit(gql, title, difficulty, positiveCount, negativeCount)
                            onDismissRequest()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add")
                }
            }
        }
    }
}

suspend fun createHabit(
    gql: ApolloClient,
    title: String,
    difficulty: Int,
    positiveCount: Boolean,
    negativeCount: Boolean
) {
    try {
        gql.mutate(
            CreateHabitMutation(CreateHabitInput(title, difficulty, positiveCount, negativeCount))
        ).await()
        gql.query(HabitsQuery()).await()
    } catch (e: ApolloNetworkException) {
    }
}