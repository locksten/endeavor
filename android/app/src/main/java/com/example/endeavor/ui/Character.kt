package com.example.endeavor.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.endeavor.LocalAuth


@Composable
fun Character() {
    val auth = LocalAuth.current
    val loggedInUsername by auth.loggedInUsernameState()

    Column {
        Text("username: $loggedInUsername")
        Button(onClick = {
            auth.logOut()
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Log out")
        }
    }
}