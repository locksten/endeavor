package com.example.endeavor

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.endeavor.ui.theme.EndeavorTheme
import com.example.endeavor.ui.theme.Theme

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