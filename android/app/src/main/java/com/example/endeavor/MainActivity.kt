package com.example.endeavor

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
    val client = LocalGQLClient.current
    val auth = LocalAuth.current
    var username by remember { mutableStateOf("alice") }
    var password by remember { mutableStateOf("passwordpassword") }
    var error by remember { mutableStateOf<String?>(null) }
    val isLoggedIn by auth.isLoggedIn()
    val token by auth.authTokenState()
    val loggedInUsername by auth.loggedInUsernameState()

    Column {
        Text("token: $token")
        Text("username: $loggedInUsername")
        if (isLoggedIn) {
            Button(onClick = {
                auth.logOut()
            }) {
                Text("Log out")
            }
        } else {
            Column {
                TextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") }
                )
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)

                )
                error?.let {
                    Text(it)
                }
                Button(onClick = {
                    scope.launch {
                        error = auth.logIn(client, username, password)
                    }
                }) {
                    Text("Log in")
                }
            }
        }

    }
}