package com.lebartodev.lnote.show


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lebartodev.core.utils.SchedulersFacade
import javax.inject.Inject


class ShowNoteViewModelFactory @Inject constructor(private val schedulersFacade: SchedulersFacade) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when (modelClass) {
            ShowNoteViewModel::class.java -> ShowNoteViewModel(schedulersFacade) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}