package com.example.endeavor.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloNetworkException
import com.example.endeavor.DeleteTaskMutation
import com.example.endeavor.LocalGQLClient
import com.example.endeavor.TasksQuery
import com.example.endeavor.ui.theme.Theme
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@Composable
fun DeleteWithConfirmationButton(onDelete: () -> Unit) {
    val scope = rememberCoroutineScope()
    var tapped by remember { mutableStateOf(false) }

    Button(
        onClick = {
            scope.launch {
                if (tapped) {
                    onDelete()
                } else {
                    tapped = true
                }
            }
        },
        modifier = Modifier.fillMaxWidth(),
        colors = if (tapped) ButtonDefaults.buttonColors(
            backgroundColor = Theme.colors.danger,
            contentColor = Theme.colors.onDanger
        ) else {
            ButtonDefaults.buttonColors()
        }
    ) {
        Text(
            if (tapped) {
                "Really Delete"
            } else {
                "Delete"
            }
        )
    }
}