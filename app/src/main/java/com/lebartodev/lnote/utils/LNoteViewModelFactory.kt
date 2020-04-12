package com.lebartodev.lnote.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lebartodev.lnote.common.details.ShowNoteViewModel
import com.lebartodev.lnote.common.edit.NoteEditViewModel
import com.lebartodev.lnote.common.notes.NotesViewModel
import com.lebartodev.lnote.data.Manager
import com.lebartodev.lnote.repository.NotesRepository
import javax.inject.Inject


class LNoteViewModelFactory @Inject constructor(private val notesRepository: NotesRepository,
                                                private val settingsManager: Manager.Settings,
                                                private val schedulersFacade: SchedulersFacade,
                                                private val currentNoteManager: Manager.CurrentNote) : ViewModelProvider.Factory {
    private val editViewModel by lazy { NoteEditViewModel(notesRepository, settingsManager, schedulersFacade, currentNoteManager) }
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when (modelClass) {
            NotesViewModel::class.java ->  NotesViewModel(notesRepository, schedulersFacade) as T
            NoteEditViewModel::class.java -> editViewModel as T
            ShowNoteViewModel::class.java -> ShowNoteViewModel(notesRepository, schedulersFacade) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}