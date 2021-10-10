package com.example.endeavor.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
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

val materialDarkColors = darkColors(
    primary = MyColors(true).primary,
    onPrimary = MyColors(true).onPrimary,

    secondary = MyColors(true).secondary,
    onSecondary = MyColors(true).onSecondary,

    background = MyColors(true).background,
    onBackground = MyColors(true).onBackground,

    surface = MyColors(true).surface,
    onSurface = MyColors(true).onSurface,
)

val materialLightColors = lightColors(
    primary = MyColors(false).primary,
    onPrimary = MyColors(false).onPrimary,

    secondary = MyColors(false).secondary,
    onSecondary = MyColors(false).onSecondary,

    background = MyColors(false).background,
    onBackground = MyColors(false).onBackground,

    surface = MyColors(false).surface,
    onSurface = MyColors(false).onSurface,
)

@Composable
fun EndeavorTheme(isDarkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalMyColors provides MyColors(isDarkTheme)
    ) {
        MaterialTheme(
            typography = Typography,
            colors = if (isDarkTheme) {
                materialDarkColors
            } else {
                materialLightColors
            },
            shapes = Shapes,
            content = content
        )
    }
}