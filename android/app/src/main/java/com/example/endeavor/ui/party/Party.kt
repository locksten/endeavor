package com.example.endeavor.ui.party

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch

sealed class PartyTab(
    val label: String,
    val composable: @Composable () -> Unit
) {
    @ExperimentalMaterialApi
    @ExperimentalFoundationApi
    @ExperimentalComposeUiApi
    object Party : PartyTab("Party", { CPartyMemberList() })

    @ExperimentalFoundationApi
    @ExperimentalMaterialApi
    @ExperimentalComposeUiApi
    object Invitees : PartyTab("Invitees", { CInviteeList() })

    @ExperimentalMaterialApi
    @ExperimentalFoundationApi
    @ExperimentalComposeUiApi
    object Inviters : PartyTab("Inviters", { CInviterList()})
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
val partyTabs = listOf(PartyTab.Party, PartyTab.Invitees, PartyTab.Inviters)

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
        partyTabs.forEachIndexed { index, tab ->
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
fun PartyScreen() {
    val pagerState = rememberPagerState(0)
    Column {
        Tabs(pagerState)
        HorizontalPager(
            count = partyTabs.size,
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) {
            partyTabs[it].composable()
        }
    }

}