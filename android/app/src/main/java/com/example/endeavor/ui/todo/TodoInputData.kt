package com.example.endeavor.ui.todo

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.apollographql.apollo.api.Input
import com.example.endeavor.type.CreateTodoInput
import com.example.endeavor.type.UpdateTodoInput
import com.example.endeavor.ui.AppTextField

data class TodoInputData(
    val title: Input<String> = Input.absent(),
    val difficulty: Input<Int> = Input.absent(),
) {
    constructor(title: String, difficulty: Int) : this(
        Input.fromNullable(title),
        Input.fromNullable(difficulty)
    )

    companion object {
        fun defaultValues(): TodoInputData {
            return TodoInputData(
                title = Input.fromNullable(""),
                difficulty = Input.fromNullable(50),
            )
        }
    }
}

fun TodoInputData.toCreateTodoInput() = CreateTodoInput(
    title = title.value ?: TodoInputData.defaultValues().title.value!!,
    difficulty = difficulty.value ?: TodoInputData.defaultValues().difficulty.value!!
)

fun TodoInputData.toUpdateTodoInput() = UpdateTodoInput(
    title = title,
    difficulty = difficulty
)

@Composable
fun TodoInput(
    defaultValue: TodoInputData = TodoInputData.defaultValues(),
    value: TodoInputData,
    onChange: (TodoInputData) -> Unit,
    onDone: () -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Words),
) {
    val titleFocusRequester = remember { FocusRequester() }
    LaunchedEffect(true) {
        titleFocusRequester.requestFocus()
    }
    AppTextField(
        value.title.value ?: defaultValue.title.value!!, titleFocusRequester, label = "Title",
        keyboardActions = KeyboardActions(onDone = {
            onDone()
        }),
        keyboardOptions = keyboardOptions
    ) { onChange(value.copy(title = Input.fromNullable(it))) }
    TodoDifficultySelector(
        value = value.difficulty.value ?: defaultValue.difficulty.value!!,
        onChange = { onChange(value.copy(difficulty = Input.fromNullable(it))) })

}