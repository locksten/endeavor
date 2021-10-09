package com.example.endeavor

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.endeavor.ui.theme.EndeavorTheme
import com.example.endeavor.ui.theme.Theme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EndeavorTheme {
                EndeavorGQL {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(bottomBar = {
        BottomNav(
            navController.currentDestination?.navigatorName
        ) { destination -> navController.navigate(route = destination) }
    }) {
        NavHost(navController, startDestination = "A") {
            composable("A") { A() }
            composable("B") { B() }
            composable("auth") { AuthScreen() }
        }
    }
}


@Composable
fun A() {
    Box(
        Modifier
            .fillMaxSize()
            .background(color = Theme.colors.background)
    ) { CUserSearchList("a") }
}

@Composable
fun B() {
    Box(
        Modifier
            .fillMaxSize()
            .background(color = Theme.colors.background)
    ) { CRNG() }
}

@Composable
fun C() {
    Box(
        Modifier
            .fillMaxSize()
            .background(color = Theme.colors.background)
    ) { CUserSearchList("a") }
}

@Composable
fun BottomNav(selected: String? = "A", onClick: (destination: String) -> Unit) {
    BottomNavigation {
        BottomNavigationItem(
            label = { Text("A") },
            selected = selected == "A",
            onClick = { onClick("A") },
            icon = { Text("A") })
        BottomNavigationItem(
            label = { Text("B") },
            selected = selected == "A",
            onClick = { onClick("B") },
            icon = { Text("B") })
        BottomNavigationItem(
            label = { Text("Auth") },
            selected = selected == "A",
            onClick = { onClick("auth") },
            icon = { Text("A") })
    }
}

@Composable
fun CRNG() {
    val num = gqlWatchQuery(RNGQuery())?.rngNum

    Log.i("endeavor", "rng " + num.toString())

    Column {
        if (num != null) {
            Text(
                text = num.toString(),
                color = Theme.colors.text,
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp
            )
        }
        Button(onClick = {
        }) {
            Text("Do Something...")
        }
    }

}

@Composable
fun AuthScreen() {
    val scope = rememberCoroutineScope()
    val passwordFocusRequester = remember { FocusRequester() }
    val client = LocalGQLClient.current
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
    val isLoggedIn by auth.isLoggedIn()
    val token by auth.authTokenState()
    val loggedInUsername by auth.loggedInUsernameState()

    if (isLoggedIn) {
        Column {
            Text("token: $token")
            Text("username: $loggedInUsername")
            Button(onClick = {
                auth.logOut()
            }) {
                Text("Log out")
            }
        }
    } else {
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
                            error = auth.logIn(client, username, password.text)
                        }
                    }),
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Button(
                        onClick = {
                            scope.launch {
                                error = auth.register(client, username, password.text)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Sign Up")
                    }
                    Button(
                        onClick = {
                            scope.launch {
                                error = auth.logIn(client, username, password.text)
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