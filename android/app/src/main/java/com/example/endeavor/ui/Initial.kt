package com.example.endeavor.ui

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.example.endeavor.LocalAuth

@Composable
fun InitialScreen() {
    val isLoggedIn by LocalAuth.current.isLoggedIn()
    when (isLoggedIn) {
        null -> Text("Loading...")
        true -> MainScreen()
        else -> AuthScreen()
    }
}