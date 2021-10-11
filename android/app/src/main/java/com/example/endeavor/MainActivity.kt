package com.example.endeavor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.toArgb
import com.example.endeavor.ui.InitialScreen
import com.example.endeavor.ui.theme.EndeavorTheme
import com.example.endeavor.ui.theme.Theme
import com.google.accompanist.pager.ExperimentalPagerApi

class MainActivity : ComponentActivity() {
    @ExperimentalComposeUiApi
    @ExperimentalPagerApi
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EndeavorTheme {
                val colors = Theme.colors
                SideEffect {
                    this.window.statusBarColor = colors.primary.toArgb()
                }
                EndeavorGQL {
                    InitialScreen()
                }
            }
        }
    }
}