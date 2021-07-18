package com.lebartodev.lnote.feature_attach

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lebartodev.core.utils.SchedulersFacade
import com.lebartodev.lnote.feature_attach.di.AttachScope
import javax.inject.Inject

@AttachScope
class AttachViewModelFactory @Inject constructor(private val filesRepository: FilesRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when (modelClass) {
            AttachViewModel::class.java -> AttachViewModel(filesRepository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}