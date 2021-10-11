package com.example.endeavor.ui

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.endeavor.LocalAuth
import com.example.endeavor.RNGQuery
import com.example.endeavor.gqlWatchQuery
import com.google.accompanist.pager.ExperimentalPagerApi

sealed class MainScreenTab(val route: String, val label: String, val icon: ImageVector) {
    object Todos : MainScreenTab("todos", "Todos", Icons.Rounded.List)
    object Character : MainScreenTab("character", "CharacterScreen", Icons.Rounded.Person)
}

@ExperimentalComposeUiApi
@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    Scaffold(bottomBar = {
        BottomNav(
            currentDestination?.route
        ) { destination ->
            navController.navigate(route = destination) {
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding())
        )
        {
            NavHost(
                navController,
                startDestination = MainScreenTab.Character.route,
            ) {
                composable(MainScreenTab.Todos.route) { TodosScreen() }
                composable(MainScreenTab.Character.route) { CharacterScreen() }
            }
        }
    }
}

val mainTabs = listOf(MainScreenTab.Todos)

@Composable
fun BottomNav(
    selected: String? = MainScreenTab.Character.route,
    onClick: (String) -> Unit
) {
    val username = LocalAuth.current.loggedInUsernameState().value ?: "CharacterScreen"
    BottomNavigation {
        mainTabs.forEach { tab ->
            BottomNavigationItem(
                label = { Text(tab.label) },
                selected = selected == tab.route,
                onClick = { onClick(tab.route) },
                icon = { Icon(tab.icon, tab.label) })
        }
        BottomNavigationItem(
            label = { Text(username) },
            selected = selected == MainScreenTab.Character.route,
            onClick = { onClick(MainScreenTab.Character.route) },
            icon = { Icon(MainScreenTab.Character.icon, username) })
    }
}