package com.lebartodev.lnote.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lebartodev.core.data.repository.Repository
import com.lebartodev.core.utils.SchedulersFacade
import io.reactivex.disposables.Disposable

class SettingsViewModel(private val settingsRepository: Repository.Settings, schedulersFacade: SchedulersFacade) : ViewModel() {
    private val bottomPanelEnabledLiveData = MutableLiveData<Boolean>()
    private val d: Disposable = settingsRepository.bottomPanelEnabled()
            .observeOn(schedulersFacade.ui())
            .subscribe { bottomPanelEnabledLiveData.value = it }

    fun bottomPanelEnabled(): LiveData<Boolean> = bottomPanelEnabledLiveData

    fun setBottomPanelEnabled(value: Boolean) {
        settingsRepository.setBottomPanelEnabled(value)
        bottomPanelEnabledLiveData.value = value
    }

    override fun onCleared() {
        super.onCleared()
        d.dispose()
    }
}
