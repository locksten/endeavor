package com.example.endeavor

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class DarkMode(private val context: Context) {
    private val isDarkMode = MutableStateFlow(getPersistedIsDarkMode())

    @Composable
    fun isDarkModeState(): State<Boolean> {
        val isSystemDarkMode = isSystemInDarkTheme()
        return isDarkMode.map { it ?: isSystemDarkMode }.collectAsState(isSystemDarkMode)
    }

    @Composable
    fun isDarkModePrefState(): State<Boolean?> {
        return isDarkMode.collectAsState(null)
    }

    private fun getPersistedIsDarkMode(): Boolean? {
        return getSharedPrefBoolean(context, PrefKey.DarkMode)
    }

    fun cycleIsDarkMode() {
        when (isDarkMode.value) {
            null -> setIsDarkMode(true)
            true -> setIsDarkMode(false)
            false -> unsetIsDarkMode()
        }
    }

    private fun setIsDarkMode(value: Boolean) {
        setSharedPrefBoolean(context, PrefKey.DarkMode, value)
        isDarkMode.value = value
    }

    private fun unsetIsDarkMode() {
        removeSharedPref(context, PrefKey.DarkMode)
        isDarkMode.value = null
    }

}