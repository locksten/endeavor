package com.example.endeavor.ui.todo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.endeavor.ui.theme.Theme

@Composable
fun TodoDifficultySelector(
    value: Int,
    onChange: (Int) -> Unit
) {

    @Composable
    fun DifficultySelectorIcon() {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(Theme.colors.onDifficultyButton)
        )
    }

    @Composable
    fun RowScope.DifficultySelectorButton(
        buttonValue: Int,
        text: String? = null
    ) {
        Button(
            onClick = { onChange(buttonValue) },
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 2.dp),
            shape = RoundedCornerShape(0.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = if (value == buttonValue) {
                    Theme.colors.difficultyButtonActive
                } else {
                    Theme.colors.difficultyButton
                },
                contentColor = Theme.colors.onDifficultyButton
            )
        ) {
            if (text == null) {
                DifficultySelectorIcon()
            } else {
                Text(text, maxLines = 1)
            }
        }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(1.dp),
        modifier = Modifier.clip(RoundedCornerShape(4.dp))
    ) {
        DifficultySelectorButton(buttonValue = 10, text = "Easy")
        DifficultySelectorButton(buttonValue = 25)
        DifficultySelectorButton(buttonValue = 50)
        DifficultySelectorButton(buttonValue = 75)
        DifficultySelectorButton(buttonValue = 90, text = "Hard")
    }
}