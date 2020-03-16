package com.lebartodev.lnote.repository

import android.content.SharedPreferences
import com.lebartodev.lnote.utils.DebugOpenClass

@DebugOpenClass
class SettingsRepository constructor(val preferences: SharedPreferences) {
    fun setBottomPanelEnabled(value: Boolean) {
        val editor = preferences.edit()
        editor.putBoolean(KEY_BOTTOM_PANEL, value)
        editor.apply()
    }

    fun isBottomPanelEnabled(): Boolean {
        return preferences.getBoolean(KEY_BOTTOM_PANEL, true)
}

    companion object {
        private const val KEY_BOTTOM_PANEL = "KEY_BOTTOM_PANEL"
    }
}