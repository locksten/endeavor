package com.example.endeavor.ui.party

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloNetworkException
import com.example.endeavor.*
import com.example.endeavor.ui.theme.Theme
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@Composable
fun CInviteModal(onDismissRequest: () -> Unit) {
    var username by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(true) {
        focusRequester.requestFocus()
    }

    MutationComposable { gql, scope ->
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
                    TextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        colors = TextFieldDefaults.textFieldColors(textColor = Theme.colors.onBackground)
                    )
                    Button(
                        onClick = {
                            scope.launch {
                                invite(gql, username)
                                onDismissRequest()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Invite")
                    }
                }
            }
        }
    }
}

suspend fun invite(gql: ApolloClient, username: String) {
    try {
        gql.mutate(
            InviteMutation(username)
        ).await()
        gql.query(InviteesQuery()).await()
    } catch (e: ApolloNetworkException) {
    }
}