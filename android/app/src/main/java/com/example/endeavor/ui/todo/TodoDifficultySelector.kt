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
import androidx.compose.ui.graphics.Shape
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
        Content: @Composable () -> Unit = { DifficultySelectorIcon() },
        shape: Shape
    ) {
        Button(
            onClick = { onChange(buttonValue) },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 1.dp),
            contentPadding = PaddingValues(horizontal = 1.dp),
            shape = shape,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = if (value == buttonValue) {
                    Theme.colors.difficultyButtonActive
                } else {
                    Theme.colors.difficultyButton
                },
                contentColor = Theme.colors.onDifficultyButton
            )
        ) {
            Content()
        }
    }

    val leftShape = RoundedCornerShape(4.dp, 0.dp, 0.dp, 4.dp)
    val middleShape = RoundedCornerShape(0.dp)
    val rightShape = RoundedCornerShape(0.dp, 4.dp, 4.dp, 0.dp)

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        DifficultySelectorButton(buttonValue = 10, Content = { Text("Easy") }, shape = leftShape)
        DifficultySelectorButton(buttonValue = 25, shape = middleShape)
        DifficultySelectorButton(buttonValue = 50, shape = middleShape)
        DifficultySelectorButton(buttonValue = 75, shape = middleShape)
        DifficultySelectorButton(buttonValue = 90, Content = { Text("Hard") }, shape = rightShape)
    }
}