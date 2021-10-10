package com.example.endeavor.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.endeavor.LocalAuth
import com.example.endeavor.LocalGQLClient
import com.example.endeavor.ui.theme.Theme
import kotlinx.coroutines.launch

@Composable
fun AuthScreen() {
    val scope = rememberCoroutineScope()
    val passwordFocusRequester = remember { FocusRequester() }
    val gql = LocalGQLClient.current
    val auth = LocalAuth.current
    var username by remember { mutableStateOf("alice") }
    var password by remember {
        mutableStateOf(
            TextFieldValue(
                text = "passwordpassword",
                selection = TextRange(16, 16)
            )
        )
    }
    var error by remember { mutableStateOf<String?>(null) }

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .padding(top = 64.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .alpha(if (error == null) 0f else 1f)
                        .background(Theme.colors.onDanger)
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = error ?: "",
                        color = Theme.colors.danger,
                        fontWeight = FontWeight.Bold
                    )
                }
                TextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = {
                        passwordFocusRequester.requestFocus()
                    })
                )
                TextField(
                    value = password,
                    onValueChange = {
                        password = it
                    },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Go
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(passwordFocusRequester),
                    singleLine = true,
                    keyboardActions = KeyboardActions(onGo = {
                        scope.launch {
                            error = auth.logIn(gql, username, password.text)
                        }
                    }),
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Button(
                        onClick = {
                            scope.launch {
                                error = auth.register(gql, username, password.text)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Sign Up")
                    }
                    Button(
                        onClick = {
                            scope.launch {
                                error = auth.logIn(gql, username, password.text)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Log In")
                    }
                }
            }
        }

    }
}