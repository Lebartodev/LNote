package com.lebartodev.lnote.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lebartodev.lnote.common.details.ShowNoteViewModel
import com.lebartodev.lnote.common.edit.NoteEditViewModel
import com.lebartodev.lnote.common.notes.NotesViewModel
import com.lebartodev.lnote.data.CurrentNoteManager
import com.lebartodev.lnote.repository.NotesRepository
import com.lebartodev.lnote.repository.SettingsRepository
import javax.inject.Inject


class LNoteViewModelFactory @Inject constructor(private val notesRepository: NotesRepository,
                                                private val settingsRepository: SettingsRepository,
                                                private val schedulersFacade: SchedulersFacade,
                                                private val currentNoteManager: CurrentNoteManager) : ViewModelProvider.Factory {
    private val editViewModel by lazy { NoteEditViewModel(notesRepository, settingsRepository, schedulersFacade, currentNoteManager) }
    private val notesViewModel by lazy { NotesViewModel(notesRepository, schedulersFacade) }
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when (modelClass) {
            NotesViewModel::class.java -> notesViewModel as T
            NoteEditViewModel::class.java -> editViewModel as T
            ShowNoteViewModel::class.java -> ShowNoteViewModel(notesRepository, schedulersFacade) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}