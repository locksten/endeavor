package com.example.endeavor.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf


private val LocalMyRawColors = staticCompositionLocalOf { MyRawColors }
private val LocalMyColors = staticCompositionLocalOf { MyColors(false) }

object Theme {
    val rawColors
        @Composable
        @ReadOnlyComposable
        get() = LocalMyRawColors.current
    val colors
        @Composable
        @ReadOnlyComposable
        get() = LocalMyColors.current
    val material = MaterialTheme
}


@Composable
fun EndeavorTheme(isDarkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalMyColors provides MyColors(isDarkTheme)
    ) {
        MaterialTheme(
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}