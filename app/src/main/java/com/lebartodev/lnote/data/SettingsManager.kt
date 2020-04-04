package com.lebartodev.lnote.data

import android.content.SharedPreferences
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

class SettingsManager @Inject constructor(private val preferences: SharedPreferences) : Manager.Settings {
    private val bottomPanelSubject: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(isBottomPanelEnabled())

    override fun setBottomPanelEnabled(value: Boolean) {
        val editor = preferences.edit()
        editor.putBoolean(KEY_BOTTOM_PANEL, value)
        editor.apply()
        bottomPanelSubject.onNext(value)
    }

    private fun isBottomPanelEnabled(): Boolean {
        return preferences.getBoolean(KEY_BOTTOM_PANEL, true)
    }

    override fun bottomPanelEnabled(): Observable<Boolean> = bottomPanelSubject

    companion object {
        private const val KEY_BOTTOM_PANEL = "KEY_BOTTOM_PANEL"
    }
}