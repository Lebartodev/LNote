package com.lebartodev.lnote.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lebartodev.core.data.repository.Repository
import com.lebartodev.core.utils.SchedulersFacade
import javax.inject.Inject

class SettingsViewModelFactory @Inject constructor(
        private val settingsRepository: Repository.Settings,
        private val schedulersFacade: SchedulersFacade
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = SettingsViewModel(
            settingsRepository, schedulersFacade) as T
}