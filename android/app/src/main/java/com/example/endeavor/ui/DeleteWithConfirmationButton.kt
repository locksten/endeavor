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
fun DeleteWithConfirmationButton(onDelete: () -> Unit) {
    val scope = rememberCoroutineScope()
    var isTapped by remember { mutableStateOf(false) }

    Button(
        onClick = {
            scope.launch {
                if (isTapped) {
                    onDelete()
                } else {
                    isTapped = true
                }
            }
        },
        modifier = Modifier.fillMaxWidth(),
        colors = if (isTapped) ButtonDefaults.buttonColors(
            backgroundColor = Theme.colors.danger,
            contentColor = Theme.colors.onDanger
        ) else {
            ButtonDefaults.buttonColors()
        }
    ) {
        Text(
            if (isTapped) {
                "Really Delete"
            } else {
                "Delete"
            }
        )
    }
}