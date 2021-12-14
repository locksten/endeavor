package com.example.endeavor.ui

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.endeavor.ui.theme.Theme
import com.google.android.material.datepicker.MaterialDatePicker
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.*
import java.time.format.DateTimeFormatter

@Composable
fun DateTimeInput(label: String, value: OffsetDateTime?, onChange: (OffsetDateTime) -> Unit) {
    var localDate: LocalDate? by remember {
        mutableStateOf(
            value?.atZoneSameInstant(ZoneId.systemDefault())?.toLocalDateTime()?.toLocalDate()
        )
    }
    var localTime: LocalTime? by remember {
        mutableStateOf(
            value?.atZoneSameInstant(ZoneId.systemDefault())?.toLocalDateTime()?.toLocalTime()
        )
    }

    fun update() {
        if (localDate == null || localTime == null) return
        val localDateTime = LocalDateTime.of(localDate, localTime)
        onChange(
            OffsetDateTime.of(localDateTime, ZoneId.systemDefault().rules.getOffset(localDateTime))
        )
    }

    val dateDialogState = rememberMaterialDialogState()
    MaterialDialog(
        dialogState = dateDialogState,
        buttons = {
            positiveButton("Ok")
            negativeButton("Cancel")
        }
    ) {
        datepicker {
            localDate = it
            update()
        }
    }


    val timeDialogState = rememberMaterialDialogState()
    MaterialDialog(
        dialogState = timeDialogState,
        buttons = {
            positiveButton("Ok")
            negativeButton("Cancel")
        }
    ) {
        timepicker {
            localTime = it
            update()
        }
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, color = Theme.colors.onSurface)
        Spacer(Modifier.width(8.dp))
        Row {
            Button(modifier = Modifier.weight(1f), onClick = { dateDialogState.show() }) {
                Text(localDate?.format(DateTimeFormatter.ofPattern("MMM d")) ?: "Date")
            }
            Spacer(Modifier.width(4.dp))
            Button(modifier = Modifier.weight(1f), onClick = { timeDialogState.show() }) {
                Text("${localTime ?: "Time"}")
            }
        }
    }
}