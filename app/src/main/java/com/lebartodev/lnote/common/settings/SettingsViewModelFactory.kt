package com.lebartodev.lnote.common.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lebartodev.lnote.repository.SettingsRepository
import com.lebartodev.lnote.utils.DebugOpenClass

@DebugOpenClass
class SettingsViewModelFactory(private val settingsRepository: SettingsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = SettingsViewModel(settingsRepository) as T
}