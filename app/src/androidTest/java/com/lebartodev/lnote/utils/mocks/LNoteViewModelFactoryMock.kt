package com.lebartodev.lnote.utils.mocks

import androidx.lifecycle.ViewModel
import com.lebartodev.lnote.common.notes.NotesViewModel
import com.lebartodev.lnote.repository.NotesRepository
import com.lebartodev.lnote.utils.LNoteViewModelFactory
import org.mockito.Mockito

class LNoteViewModelFactoryMock constructor(notesRepository: NotesRepository) :
        LNoteViewModelFactory(notesRepository) {
    override val notesViewModel: NotesViewModel by lazy { Mockito.mock(NotesViewModel::class.java) }

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass == NotesViewModel::class.java) {
            return notesViewModel as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}