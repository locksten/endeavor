package com.example.endeavor.ui.todo.habit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
fun HabitTypeSelector(
    positiveValue: Boolean,
    negativeValue: Boolean,
    onChangePositive: (Boolean) -> Unit,
    onChangeNegative: (Boolean) -> Unit
) {
    @Composable
    fun RowScope.HabitTypeSelectorButton(
        buttonValue: Boolean,
        text: String,
        onClick: (Boolean) -> Unit
    ) {
        Button(
            onClick = { onClick(!buttonValue) },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(0.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = if (buttonValue) {
                    Theme.colors.difficultyButtonActive
                } else {
                    Theme.colors.difficultyButton
                },
                contentColor = Theme.colors.onDifficultyButton
            )
        ) {
            Text(text)
        }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(1.dp),
        modifier = Modifier.clip(RoundedCornerShape(4.dp))
    ) {
        HabitTypeSelectorButton(
            text = "Negative",
            buttonValue = negativeValue,
            onClick = onChangeNegative,
        )
        HabitTypeSelectorButton(
            text = "Positive",
            buttonValue = positiveValue,
            onClick = onChangePositive,
        )
    }
}