package com.example.endeavor.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@Composable
fun AppLazyColumn(
    fabPadding: Boolean = false,
    spacedBy: Dp = 4.dp,
    topPadding: Dp = 14.dp,
    scope: LazyListScope.() -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(spacedBy),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        item { Spacer(Modifier.height(topPadding)) }
        scope()
        item {
            Spacer(
                Modifier.height(
                    if (fabPadding) {
                        80.dp
                    } else {
                        14.dp
                    }
                )
            )
        }
    }
}