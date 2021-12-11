package com.example.endeavor.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun ColumnDialog(onDismissRequest: () -> Unit, Content: @Composable () -> Unit) {
    BaseDialog(onDismissRequest) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Content()
        }
    }
}