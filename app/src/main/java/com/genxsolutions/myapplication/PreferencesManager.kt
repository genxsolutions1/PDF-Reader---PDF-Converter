package com.genxsolutions.myapplication

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "app_preferences",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_FIRST_TIME = "is_first_time"
    }

    fun isFirstTime(): Boolean {
        return prefs.getBoolean(KEY_FIRST_TIME, true)
    }

    fun setFirstTimeDone() {
        prefs.edit().putBoolean(KEY_FIRST_TIME, false).apply()
    }
}
