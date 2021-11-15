package com.example.endeavor.ui

import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import com.example.endeavor.ui.theme.Theme
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@Composable
fun ButtonWithConfirmation(
    text: String,
    confirmation: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.small,
) {
    val scope = rememberCoroutineScope()
    var isTapped by remember { mutableStateOf(false) }

    Button(
        onClick = {
            scope.launch {
                if (isTapped) {
                    onClick()
                } else {
                    isTapped = true
                }
            }
        },
        modifier = modifier,
        shape = shape,
        colors = if (isTapped) ButtonDefaults.buttonColors(
            backgroundColor = Theme.colors.danger,
            contentColor = Theme.colors.onDanger
        ) else {
            ButtonDefaults.buttonColors()
        }
    ) {
        Text(
            if (isTapped) {
                confirmation
            } else {
                text
            }
        )
    }
}