package com.lebartodev.lnote.common.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lebartodev.lnote.repository.SettingsRepository

class SettingsViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {
    private val bottomPanelEnabledLiveData = MutableLiveData<Boolean>()

    init {
        bottomPanelEnabledLiveData.postValue(settingsRepository.isBottomPanelEnabled())
    }

    fun bottomPanelEnabled(): LiveData<Boolean> = bottomPanelEnabledLiveData

    fun setBottomPanelEnabled(value: Boolean) {
        settingsRepository.setBottomPanelEnabled(value)
        bottomPanelEnabledLiveData.value = value
    }
}
