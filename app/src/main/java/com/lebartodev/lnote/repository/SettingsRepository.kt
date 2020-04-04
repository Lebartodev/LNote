package com.lebartodev.lnote.repository

import android.content.SharedPreferences
import com.lebartodev.lnote.utils.SchedulersFacade
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

class SettingsRepository @Inject constructor(private val preferences: SharedPreferences, private val schedulersFacade: SchedulersFacade) : Repository.Settings {
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
            .subscribeOn(schedulersFacade.io())

    companion object {
        private const val KEY_BOTTOM_PANEL = "KEY_BOTTOM_PANEL"
    }
}