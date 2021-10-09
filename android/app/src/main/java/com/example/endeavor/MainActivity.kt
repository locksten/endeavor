package com.example.endeavor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.endeavor.ui.InitialScreen
import com.example.endeavor.ui.theme.EndeavorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EndeavorTheme {
                EndeavorGQL {
                    InitialScreen()
                }
            }
        }
    }
}