package com.lebartodev.lnote.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lebartodev.lnote.common.details.NoteEditViewModel
import com.lebartodev.lnote.common.notes.NotesViewModel
import com.lebartodev.lnote.repository.NotesRepository


open class LNoteViewModelFactory constructor(private val notesRepository: NotesRepository,
                                             private val schedulersFacade: SchedulersFacade) :
        ViewModelProvider.Factory {
    private val editViewModel by lazy { NoteEditViewModel(notesRepository, schedulersFacade) }
    private val notesViewModel by lazy { NotesViewModel(notesRepository, schedulersFacade) }
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass == NotesViewModel::class.java) {
            return notesViewModel as T
        } else if (modelClass == NoteEditViewModel::class.java) {
            return editViewModel as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}