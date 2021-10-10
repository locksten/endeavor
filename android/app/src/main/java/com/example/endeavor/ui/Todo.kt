package com.example.endeavor.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch


sealed class TodoTab(
    val label: String,
    val creationModal: @Composable (onDismissRequest: () -> Unit) -> Unit,
    val composable: @Composable () -> Unit
) {
    @ExperimentalComposeUiApi
    object Habits : TodoTab("Habits", { CCreateTaskModal(it) }, { Text("Habits") })
    @ExperimentalComposeUiApi
    object Dailies : TodoTab("Dailies", { CCreateTaskModal(it) }, { Text("Dailies") })
    @ExperimentalComposeUiApi
    object Tasks : TodoTab("Tasks", { CCreateTaskModal(it) }, { CTaskList() })
}

@ExperimentalComposeUiApi
val todoTabs = listOf(TodoTab.Habits, TodoTab.Dailies, TodoTab.Tasks)

@ExperimentalComposeUiApi
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

@ExperimentalComposeUiApi
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

@ExperimentalComposeUiApi
@Composable
fun FloatingAddButton(tab: Int) {
    var isDialogOpen by remember { mutableStateOf(false) }
    FloatingActionButton(
        onClick = { isDialogOpen = true },
    ) {
        Icon(Icons.Filled.Add, "Create")
        if (isDialogOpen) todoTabs[tab].creationModal { isDialogOpen = false }
    }
}