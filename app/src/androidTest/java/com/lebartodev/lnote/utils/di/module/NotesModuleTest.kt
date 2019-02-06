package com.lebartodev.lnote.utils.di.module

import com.lebartodev.lnote.di.module.NotesModule
import com.lebartodev.lnote.repository.NotesRepository
import com.lebartodev.lnote.utils.LNoteViewModelFactory
import com.lebartodev.lnote.utils.mocks.LNoteViewModelFactoryMock


class NotesModuleTest : NotesModule() {
    override fun provideLNotesViewModelFactory(notesRepository: NotesRepository): LNoteViewModelFactory = LNoteViewModelFactoryMock(notesRepository)
}
