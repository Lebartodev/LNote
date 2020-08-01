package com.lebartodev.lnote.common.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lebartodev.lnote.repository.SettingsRepository
import com.lebartodev.lnote.utils.SchedulersFacade
import javax.inject.Inject

class SettingsViewModelFactory @Inject constructor(private val settingsRepository: SettingsRepository, private val schedulersFacade: SchedulersFacade) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = SettingsViewModel(settingsRepository, schedulersFacade) as T
}