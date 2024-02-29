package com.lebartodev.lnote.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lebartodev.core.data.repository.Repository
import javax.inject.Inject

class SettingsViewModelFactory @Inject constructor(
        private val settingsRepository: Repository.Settings
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = SettingsViewModel(settingsRepository) as T
}