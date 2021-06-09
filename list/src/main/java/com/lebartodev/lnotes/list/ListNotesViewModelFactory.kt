package com.lebartodev.lnotes.list


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lebartodev.core.data.repository.Repository
import com.lebartodev.core.utils.SchedulersFacade
import javax.inject.Inject


class ListNotesViewModelFactory @Inject constructor(private val rep: Repository.Notes, private val schedulersFacade: SchedulersFacade) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when (modelClass) {
            NotesViewModel::class.java -> NotesViewModel(rep, schedulersFacade) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}