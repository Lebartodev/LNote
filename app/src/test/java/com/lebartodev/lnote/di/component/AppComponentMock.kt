package com.lebartodev.lnote.di.component

import com.lebartodev.lnote.di.module.AppModuleMock
import com.lebartodev.lnote.repository.NotesRepositoryTest
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModuleMock::class])
interface AppComponentMock : AppComponent {
    fun inject(notesRepositoryTest: NotesRepositoryTest)
}