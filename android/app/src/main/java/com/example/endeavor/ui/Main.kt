package com.example.endeavor.ui

import androidx.compose.animation.core.estimateAnimationDurationMillis
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.endeavor.LocalAuth
import com.example.endeavor.globalAppMessageHandler
import com.example.endeavor.ui.battle.CBattleOrCreatureScreen
import com.example.endeavor.ui.party.PartyScreen
import com.example.endeavor.ui.reward.CRewardList
import com.example.endeavor.ui.todo.TodosScreen
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.launch

sealed class MainScreenTab(val route: String, val label: String, val icon: ImageVector) {
    object Todos : MainScreenTab("todos", "Todos", Icons.Rounded.List)
    object Shop : MainScreenTab("shop", "Shop", Icons.Rounded.Home)
    object Battle : MainScreenTab("battle", "Battle", Icons.Rounded.Place)
    object Party : MainScreenTab("party", "Party", Icons.Rounded.AccountCircle)
    object Character : MainScreenTab("character", "CharacterScreen", Icons.Rounded.Face)
}

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun MainScreen() {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    globalAppMessageHandler = { msg ->
        scope.launch {
            val title = msg.notification?.title?.plus(" ")
            val body = msg.notification?.body
            scaffoldState.snackbarHostState.showSnackbar(
                message = "$title$body",
                duration = SnackbarDuration.Short
            )
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        bottomBar = {
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
                composable(MainScreenTab.Party.route) { PartyScreen() }
                composable(MainScreenTab.Shop.route) { CRewardList() }
                composable(MainScreenTab.Battle.route) { CBattleOrCreatureScreen() }
                composable(MainScreenTab.Character.route) { CharacterScreen() }
            }
        }
    }
}

val mainTabs =
    listOf(MainScreenTab.Todos, MainScreenTab.Shop, MainScreenTab.Battle, MainScreenTab.Party)

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