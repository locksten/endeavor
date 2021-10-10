package com.example.endeavor.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.endeavor.LocalAuth
import com.example.endeavor.ui.theme.LocalMyDarkMode


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
    val username = LocalAuth.current.loggedInUsernameState().value ?: ""
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = username,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }

}

@Composable
private fun Settings() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(8.dp)
    ) {
        LogOutButton()
        ThemeButton()
    }
}


@Composable
private fun ThemeButton() {
    val darkMode = LocalMyDarkMode.current
    Button(
        onClick = {
            darkMode.cycleIsDarkMode()
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            when (darkMode.isDarkModePrefState().value) {
                null -> "System Theme"
                true -> "Dark Theme"
                false -> "Light Theme"
            }
        )
    }
}

@Composable
private fun LogOutButton() {
    val auth = LocalAuth.current
    Button(
        onClick = {
            auth.logOut()
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Log out")
    }
}