package com.lebartodev.lnote.repository.di.component

import com.lebartodev.lnote.di.component.NotesComponent
import com.lebartodev.lnote.di.module.NotesModule
import com.lebartodev.lnote.repository.NotesRepositoryTest
import dagger.Subcomponent

@Subcomponent(modules = [NotesModule::class])
interface NotesComponentMock : NotesComponent {
    fun inject(notesRepositoryTest: NotesRepositoryTest)
}