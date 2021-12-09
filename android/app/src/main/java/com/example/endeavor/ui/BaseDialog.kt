package com.example.endeavor.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.endeavor.ui.theme.Theme

@Composable
fun BaseDialog(onDismissRequest: () -> Unit, Content: @Composable () -> Unit) {
    Dialog(onDismissRequest) {
        Box(
            modifier = Modifier
                .background(Theme.colors.background)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Content()
        }
    }
}