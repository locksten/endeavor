package com.example.endeavor.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
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
fun CAuthScreen() {
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
                AuthError(error)
                Username(username, { username = it }, passwordFocusRequester)
                Password(password, { password = it }, {
                    scope.launch {
                        error = auth.logIn(gql, username, password.text)
                    }
                }, passwordFocusRequester)
                AuthButtons(username, password) { error = it }
            }
        }
    }
}

@Composable
private fun AuthButtons(
    username: String,
    password: TextFieldValue,
    onError: (String?) -> Unit
) {
    val scope = rememberCoroutineScope()
    val gql = LocalGQLClient.current
    val auth = LocalAuth.current

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Button(
            onClick = {
                scope.launch {
                    onError(auth.register(gql, username, password.text))
                }
            },
            modifier = Modifier.weight(1f)
        ) {
            Text("Sign Up")
        }
        Button(
            onClick = {
                scope.launch {
                    onError(auth.logIn(gql, username, password.text))
                }
            },
            modifier = Modifier.weight(1f)
        ) {
            Text("Log In")
        }
    }
}

@Composable
private fun AuthError(error: String?) {
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
}

@Composable
private fun Username(username: String, onChange: (String) -> Unit, focusRequester: FocusRequester) {
    TextField(
        value = username,
        onValueChange = onChange,
        label = { Text("Username") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = {
            focusRequester.requestFocus()
        })
    )
}

@Composable
private fun Password(
    password: TextFieldValue,
    onChange: (TextFieldValue) -> Unit,
    onGo: () -> Unit,
    focusRequester: FocusRequester
) {
    TextField(
        value = password,
        onValueChange = onChange,
        label = { Text("Password") },
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Go
        ),
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        singleLine = true,
        keyboardActions = KeyboardActions(onGo = { onGo() }),
    )
}