package com.example.endeavor.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.apollographql.apollo.ApolloClient
import com.example.endeavor.LocalGQLClient
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch


sealed class TodoTab(
    val label: String,
    val onFabAdd: suspend (gql: ApolloClient) -> Unit,
    val composable: @Composable () -> Unit
) {
    object Habits : TodoTab("Habits", { gql -> createTask(gql) }, { Text("Habits") })
    object Dailies : TodoTab("Dailies", { gql -> createTask(gql) }, { Text("Dailies") })
    object Tasks : TodoTab("Tasks", { gql -> createTask(gql) }, { CTaskList() })
}

val todoTabs = listOf(TodoTab.Habits, TodoTab.Dailies, TodoTab.Tasks)

@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun Tabs(pagerState: PagerState) {
    val scope = rememberCoroutineScope()
    TabRow(
        selectedTabIndex = pagerState.currentPage,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                modifier = Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
            )
        }
    ) {
        todoTabs.forEachIndexed { index, tab ->
            Tab(
                text = { Text(tab.label) },
                selected = index == pagerState.currentPage,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
            )
        }
    }
}

@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun TodosScreen() {
    val pagerState = rememberPagerState()
    Scaffold(floatingActionButton = { FloatingAddButton(pagerState.currentPage) }) {
        Column {
            Tabs(pagerState)
            HorizontalPager(
                count = todoTabs.size,
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) {
                todoTabs[it].composable()
            }
        }
    }
}

@Composable
fun FloatingAddButton(tab: Int) {
    val scope = rememberCoroutineScope()
    val gql = LocalGQLClient.current
    FloatingActionButton(
        onClick = {
            scope.launch {
                todoTabs[tab].onFabAdd(gql)
            }
        },
    ) {
        Icon(Icons.Filled.Add, "Create")
    }
}