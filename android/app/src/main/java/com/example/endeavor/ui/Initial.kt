package com.example.endeavor.ui

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.example.endeavor.LocalAuth
import com.google.accompanist.pager.ExperimentalPagerApi

@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun InitialScreen() {
    val isLoggedIn by LocalAuth.current.isLoggedIn()
    when (isLoggedIn) {
        null -> Text("Loading...")
        true -> MainScreen()
        else -> AuthScreen()
    }
}