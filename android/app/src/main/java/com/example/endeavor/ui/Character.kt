package com.example.endeavor.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun CharacterScreen() {
    Column(verticalArrangement = Arrangement.SpaceBetween) {
        Box(Modifier.weight(1f)) {
            Character()
        }
        Settings()
    }
}

@Composable
private fun Character() {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Vitals()
    }
}