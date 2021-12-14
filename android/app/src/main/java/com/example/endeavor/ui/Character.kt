package com.example.endeavor.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import com.example.endeavor.ui.item.CInventoryList
import com.example.endeavor.ui.party.CInviteeList
import com.example.endeavor.ui.party.CInviterList
import com.example.endeavor.ui.party.CPartyMemberList
import com.example.endeavor.ui.party.PartyTab
import com.google.accompanist.pager.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch


@ExperimentalCoroutinesApi
@Composable
fun CSettings() {
    Column(verticalArrangement = Arrangement.SpaceBetween) {
        Settings()
    }
}

@ExperimentalCoroutinesApi
@Composable
private fun Character() {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        CVitals()
    }
}

sealed class CharacterTab(
    val label: String,
    val composable: @Composable () -> Unit
) {
    @ExperimentalCoroutinesApi
    @ExperimentalMaterialApi
    @ExperimentalFoundationApi
    @ExperimentalComposeUiApi
    object Character : CharacterTab("Character", { Character() })

    @ExperimentalCoroutinesApi
    @ExperimentalMaterialApi
    @ExperimentalFoundationApi
    @ExperimentalComposeUiApi
    object Inventory : CharacterTab("Inventory", { CInventoryList() })

    @ExperimentalCoroutinesApi
    @ExperimentalMaterialApi
    @ExperimentalFoundationApi
    @ExperimentalComposeUiApi
    object Settings : CharacterTab("Settings", { CSettings() })
}

@ExperimentalCoroutinesApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
val inventoryTabs = listOf(CharacterTab.Character, CharacterTab.Inventory, CharacterTab.Settings)
@ExperimentalCoroutinesApi
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
        inventoryTabs.forEachIndexed { index, tab ->
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

@ExperimentalCoroutinesApi
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun CharacterScreen() {
    val pagerState = rememberPagerState(0)
    Column {
        Tabs(pagerState)
        HorizontalPager(
            count = inventoryTabs.size,
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) {
            Box(Modifier.fillMaxSize()) {
                inventoryTabs[it].composable()
            }
        }
    }
}