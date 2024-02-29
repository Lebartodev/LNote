package com.lebartodev.lnote.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lebartodev.core.data.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class SettingsViewModel(private val settingsRepository: Repository.Settings) : ViewModel() {
    private val bottomPanelEnabledLiveData = MutableLiveData<Boolean>()

    init {
        settingsRepository.bottomPanelEnabled()
            .flowOn(Dispatchers.IO)
            .onEach { bottomPanelEnabledLiveData.value = it }
            .launchIn(viewModelScope)
    }

    fun bottomPanelEnabled(): LiveData<Boolean> = bottomPanelEnabledLiveData

    fun setBottomPanelEnabled(value: Boolean) {
        settingsRepository.setBottomPanelEnabled(value)
        bottomPanelEnabledLiveData.value = value
    }
}
