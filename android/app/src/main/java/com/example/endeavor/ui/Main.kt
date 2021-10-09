package com.example.endeavor.ui

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.endeavor.RNGQuery
import com.example.endeavor.gqlWatchQuery
import com.example.endeavor.ui.theme.Theme

sealed class MainScreenTab(val route: String, val label: String, val icon: ImageVector) {
    object Tasks : MainScreenTab("tasks", "Tasks", Icons.Rounded.List)
    object Character : MainScreenTab("character", "Character", Icons.Rounded.Person)
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(bottomBar = {
        BottomNav(
            navController.currentDestination?.route
        ) { destination -> navController.navigate(route = destination) }
    }) {
        NavHost(navController, startDestination = MainScreenTab.Character.route) {
            composable(MainScreenTab.Tasks.route) { CUserSearchList(searchTerm = "al") }
            composable(MainScreenTab.Character.route) { AuthScreen() }
        }
    }
}

@Composable
fun BottomNav(
    selected: String? = MainScreenTab.Character.route,
    onClick: (String) -> Unit
) {
    val tabs = listOf(MainScreenTab.Tasks, MainScreenTab.Character)
    BottomNavigation {
        tabs.forEach { tab ->
            BottomNavigationItem(
                label = { Text(tab.label) },
                selected = selected == tab.route,
                onClick = { onClick(tab.route) },
                icon = { Icon(tab.icon, tab.label) })
        }
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