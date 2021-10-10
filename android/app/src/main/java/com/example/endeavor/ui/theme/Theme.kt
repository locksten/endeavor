package com.example.endeavor.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.endeavor.DarkMode


private val LocalMyRawColors = staticCompositionLocalOf { MyRawColors }
private val LocalMyColors = staticCompositionLocalOf { MyColors(false) }
val LocalMyDarkMode = staticCompositionLocalOf<DarkMode> {null!!}

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
fun EndeavorTheme(content: @Composable () -> Unit) {
    val darkMode = DarkMode(LocalContext.current)
    val isDarkMode by darkMode.isDarkModeState()
    CompositionLocalProvider(
        LocalMyColors provides MyColors(isDarkMode),
        LocalMyDarkMode provides darkMode
    ) {
        MaterialTheme(
            typography = Typography,
            colors = if (isDarkMode) {
                materialDarkColors
            } else {
                materialLightColors
            },
            shapes = Shapes,
            content = content
        )
    }
}