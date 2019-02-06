package com.lebartodev.lnote.utils.mocks

import androidx.lifecycle.ViewModel
import com.lebartodev.lnote.common.notes.NotesViewModel
import com.lebartodev.lnote.repository.NotesRepository
import com.lebartodev.lnote.utils.LNoteViewModelFactory
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy

class LNoteViewModelFactoryMock constructor(notesRepository: NotesRepository) :
        LNoteViewModelFactory(notesRepository) {
    val notesViewModel: NotesViewModel by lazy { spy<NotesViewModel>() }

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass == NotesViewModel::class.java) {
            return notesViewModel as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}