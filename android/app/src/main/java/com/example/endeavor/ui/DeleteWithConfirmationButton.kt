package com.example.endeavor.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier

@ExperimentalComposeUiApi
@Composable
fun ButtonWithConfirmationDelete(onDelete: () -> Unit) {
    ButtonWithConfirmation(
        text = "Delete",
        confirmation = "Really Delete",
        onClick = onDelete,
        modifier = Modifier.fillMaxWidth()
    )
}