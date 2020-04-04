package com.lebartodev.lnote.repository

import com.lebartodev.lnote.data.Manager
import com.lebartodev.lnote.utils.SchedulersFacade
import io.reactivex.Observable
import javax.inject.Inject

class SettingsRepository @Inject constructor(private val settingsManager: Manager.Settings, private val schedulersFacade: SchedulersFacade) : Repository.Settings {
    override fun setBottomPanelEnabled(value: Boolean) {
        settingsManager.setBottomPanelEnabled(value)
    }

    override fun bottomPanelEnabled(): Observable<Boolean> = settingsManager.bottomPanelEnabled()
            .subscribeOn(schedulersFacade.io())
}