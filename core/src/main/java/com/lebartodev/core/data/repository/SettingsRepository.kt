package com.lebartodev.core.data.repository

import android.content.SharedPreferences
import com.lebartodev.core.di.utils.AppScope
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@AppScope
class SettingsRepository @Inject constructor(private val preferences: SharedPreferences) : Repository.Settings {
    private val bottomPanelFlow: MutableStateFlow<Boolean> = MutableStateFlow(isBottomPanelEnabled())

    override fun setBottomPanelEnabled(value: Boolean) {
        val editor = preferences.edit()
        editor.putBoolean(KEY_BOTTOM_PANEL, value)
        editor.apply()
        bottomPanelFlow.value = value
    }

    private fun isBottomPanelEnabled(): Boolean {
        return preferences.getBoolean(KEY_BOTTOM_PANEL, true)
    }

    override fun bottomPanelEnabled(): Flow<Boolean> = bottomPanelFlow

    companion object {
        private const val KEY_BOTTOM_PANEL = "KEY_BOTTOM_PANEL"
    }
}