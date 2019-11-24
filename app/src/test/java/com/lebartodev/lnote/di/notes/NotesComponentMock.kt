package com.lebartodev.lnote.di.notes

import com.lebartodev.lnote.common.details.NoteEditViewModelTest
import com.lebartodev.lnote.common.notes.NotesViewModelTest
import com.lebartodev.lnote.di.notes.NotesComponent
import com.lebartodev.lnote.di.notes.NotesModule
import com.lebartodev.lnote.repository.NotesRepositoryTest
import dagger.Subcomponent

@Subcomponent(modules = [NotesModule::class])
interface NotesComponentMock : NotesComponent {
    fun inject(notesRepositoryTest: NotesRepositoryTest)
    fun inject(notesViewModelTest: NotesViewModelTest)
    fun inject(notesViewModelTest: NoteEditViewModelTest)
}