package com.lebartodev.lnote.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lebartodev.lnote.common.creation.NoteEditViewModel
import com.lebartodev.lnote.common.details.NoteDetailsViewModel
import com.lebartodev.lnote.common.notes.NotesViewModel
import com.lebartodev.lnote.repository.NotesRepository


open class LNoteViewModelFactory constructor(var notesRepository: NotesRepository) : ViewModelProvider.Factory {
    private val noteEditViewModel = NoteEditViewModel(notesRepository)
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass == NotesViewModel::class.java) {
            return NotesViewModel(notesRepository) as T
        } else if (modelClass == NoteDetailsViewModel::class.java) {
            return NoteDetailsViewModel(notesRepository) as T
        } else if (modelClass == NoteEditViewModel::class.java) {
            return noteEditViewModel as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}