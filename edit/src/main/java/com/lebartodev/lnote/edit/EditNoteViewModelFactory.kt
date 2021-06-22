package com.lebartodev.lnote.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lebartodev.core.data.repository.Repository
import com.lebartodev.core.utils.SchedulersFacade
import com.lebartodev.lnote.edit.di.EditScope
import javax.inject.Inject

@EditScope
class EditNoteViewModelFactory @Inject constructor(private val rep: Repository.Notes,
                                                   private val schedulersFacade: SchedulersFacade) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when (modelClass) {
            NoteEditViewModel::class.java -> NoteEditViewModel(rep, schedulersFacade) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}