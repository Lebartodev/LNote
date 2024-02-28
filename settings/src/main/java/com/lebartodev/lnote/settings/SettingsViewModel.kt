package com.lebartodev.lnote.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lebartodev.core.data.repository.Repository
import com.lebartodev.core.utils.SchedulersFacade
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SettingsViewModel(private val settingsRepository: Repository.Settings) : ViewModel() {
    private val bottomPanelEnabledLiveData = MutableLiveData<Boolean>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.bottomPanelEnabled()
                    .onEach { bottomPanelEnabledLiveData.value = it }
                    .collect()
        }
    }

    fun bottomPanelEnabled(): LiveData<Boolean> = bottomPanelEnabledLiveData

    fun setBottomPanelEnabled(value: Boolean) {
        settingsRepository.setBottomPanelEnabled(value)
        bottomPanelEnabledLiveData.value = value
    }
}
