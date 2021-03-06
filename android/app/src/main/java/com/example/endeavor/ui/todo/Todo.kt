package com.example.endeavor.ui.todo

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import com.example.endeavor.ui.todo.daily.CCreateDailyModal
import com.example.endeavor.ui.todo.daily.CDailyList
import com.example.endeavor.ui.todo.habit.CCreateHabitModal
import com.example.endeavor.ui.todo.habit.CHabitList
import com.example.endeavor.ui.todo.task.CCreateTaskModal
import com.example.endeavor.ui.todo.task.CTaskList
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch


sealed class TodoTab(
    val label: String,
    val creationModal: @Composable (onDismissRequest: () -> Unit) -> Unit,
    val composable: @Composable () -> Unit
) {
    @ExperimentalFoundationApi
    @ExperimentalComposeUiApi
    object Habits : TodoTab("Habits", { CCreateHabitModal(it) }, { CHabitList() })

    @ExperimentalFoundationApi
    @ExperimentalMaterialApi
    @ExperimentalComposeUiApi
    object Dailies : TodoTab("Dailies", { CCreateDailyModal(it) }, { CDailyList() })

    @ExperimentalFoundationApi
    @ExperimentalComposeUiApi
    object Tasks : TodoTab("Tasks", { CCreateTaskModal(it) }, { CTaskList() })
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
val todoTabs = listOf(TodoTab.Habits, TodoTab.Dailies, TodoTab.Tasks)

@ExperimentalFoundationApi
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

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun TodosScreen() {
    val pagerState = rememberPagerState(0)
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

@ExperimentalFoundationApi
@ExperimentalMaterialApi
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