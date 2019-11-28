package com.lebartodev.lnote.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lebartodev.lnote.common.details.NoteEditViewModel
import com.lebartodev.lnote.common.notes.NotesViewModel
import com.lebartodev.lnote.repository.NotesRepository


open class LNoteViewModelFactory constructor(private val notesRepository: NotesRepository,
                                             private val schedulersFacade: SchedulersFacade) :
        ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass == NotesViewModel::class.java) {
            return NotesViewModel(notesRepository, schedulersFacade) as T
        } else if (modelClass == NoteEditViewModel::class.java) {
            return NoteEditViewModel(notesRepository, schedulersFacade) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}