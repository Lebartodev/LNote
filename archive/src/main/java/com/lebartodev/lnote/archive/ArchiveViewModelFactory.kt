package com.lebartodev.lnote.archive


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lebartodev.core.data.repository.Repository
import com.lebartodev.core.utils.SchedulersFacade
import com.lebartodev.lnote.archive.di.ArchiveScope
import javax.inject.Inject

@ArchiveScope
class ArchiveViewModelFactory @Inject constructor(private val rep: Repository.Notes) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when (modelClass) {
            ArchiveViewModel::class.java -> ArchiveViewModel(rep) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}