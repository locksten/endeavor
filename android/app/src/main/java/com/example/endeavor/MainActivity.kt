package com.example.endeavor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
                MainScreen()
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
    ) { CUserSearchList("b") }
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