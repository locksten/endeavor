package com.example.endeavor.ui.todo.habit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloNetworkException
import com.example.endeavor.DeleteHabitMutation
import com.example.endeavor.LocalGQLClient
import com.example.endeavor.HabitsQuery
import com.example.endeavor.ui.DeleteWithConfirmationButton
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@Composable
fun DeleteHabitButton(habit: HabitsQuery.Habit, onDelete: () -> Unit) {
    val scope = rememberCoroutineScope()
    val gql = LocalGQLClient.current
    DeleteWithConfirmationButton {
        scope.launch {
            deleteHabit(gql, habit.id)
            onDelete()
        }
    }
}

suspend fun deleteHabit(gql: ApolloClient, id: String) {
    try {
        gql.mutate(DeleteHabitMutation(id)).await()
        gql.query(HabitsQuery()).await()
    } catch (e: ApolloNetworkException) {
    }
}