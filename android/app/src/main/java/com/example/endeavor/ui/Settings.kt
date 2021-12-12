package com.example.endeavor.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.endeavor.LocalAuth
import com.example.endeavor.LocalGQLClient
import com.example.endeavor.MutationComposable
import com.example.endeavor.ui.theme.LocalMyDarkMode
import kotlinx.coroutines.launch

@Composable
fun Settings() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(8.dp)
    ) {
        ThemeButton()
        LogOutButton()
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
    MutationComposable { gql, scope ->
        val auth = LocalAuth.current
        Button(
            onClick = {
                scope.launch {
                    auth.logOut(gql)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log out")
        }
    }
}