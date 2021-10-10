package com.example.endeavor

import android.content.Context
import android.content.SharedPreferences

private const val prefKeyFile = "com.example.endeavor.PREFERENCE_FILE_KEY"

enum class PrefKey(val key: String) {
    AuthToken("com.example.endeavor.AUTH_TOKEN"),
    Username("com.example.endeavor.USERNAME"),
    DarkMode("com.example.endeavor.DARK_MODE")
}

fun getSharedPreferences(context: Context): SharedPreferences {
    return context.getSharedPreferences(prefKeyFile, Context.MODE_PRIVATE)
}

fun getSharedPrefString(context: Context, prefKey: PrefKey): String? {
    return if (getSharedPreferences(context).contains(prefKey.key)) {
        getSharedPreferences(context).getString(prefKey.key, null)
    } else {
        null
    }
}

fun getSharedPrefBoolean(context: Context, prefKey: PrefKey): Boolean? {
    return if (getSharedPreferences(context).contains(prefKey.key)) {
        getSharedPreferences(context).getBoolean(prefKey.key, false)
    } else {
        null
    }
}

fun setSharedPrefString(context: Context, prefKey: PrefKey, value: String?) {
    with(getSharedPreferences(context).edit()) {
        putString(prefKey.key, value)
        apply()
    }
}

fun setSharedPrefBoolean(context: Context, prefKey: PrefKey, value: Boolean) {
    with(getSharedPreferences(context).edit()) {
        putBoolean(prefKey.key, value)
        apply()
    }
}

fun removeSharedPref(context: Context, prefKey: PrefKey) {
    with(getSharedPreferences(context).edit()) {
        remove(prefKey.key)
        apply()
    }
}