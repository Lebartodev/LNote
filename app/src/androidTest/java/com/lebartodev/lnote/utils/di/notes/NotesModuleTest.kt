package com.lebartodev.lnote.utils.di.notes

import com.lebartodev.lnote.di.notes.NotesModule
import com.lebartodev.lnote.repository.NotesRepository
import com.lebartodev.lnote.utils.LNoteViewModelFactory
import com.lebartodev.lnote.utils.mocks.LNoteViewModelFactoryMock


class NotesModuleTest : NotesModule() {
    override fun provideLNotesViewModelFactory(notesRepository: NotesRepository): LNoteViewModelFactory = LNoteViewModelFactoryMock(notesRepository)
}
