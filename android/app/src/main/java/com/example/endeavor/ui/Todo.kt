package com.example.endeavor.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch

val todoTabs = listOf("Habits", "Dailies", "Tasks")

@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun Tabs(pagerState: PagerState) {
    val scope = rememberCoroutineScope()
    TabRow(
        selectedTabIndex = pagerState.currentPage,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
            )
        }
    ) {
        todoTabs.forEachIndexed { index, tab ->
            Tab(
                text = { Text(tab) },
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
    Scaffold {
        Column {
            Tabs(pagerState)
            HorizontalPager(
                count = todoTabs.size,
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                when (page) {
                    0 -> Text("Habits")
                    1 -> Text("Dailies")
                    2 -> CTaskList()
                }
            }

        }
    }
}