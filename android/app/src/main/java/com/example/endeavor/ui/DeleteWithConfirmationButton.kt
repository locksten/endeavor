package com.example.endeavor.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import com.example.endeavor.ui.theme.Theme
import kotlinx.coroutines.launch

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