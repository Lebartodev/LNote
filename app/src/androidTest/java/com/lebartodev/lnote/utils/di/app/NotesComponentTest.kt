package com.lebartodev.lnote.utils.di.app

import com.lebartodev.lnote.di.notes.NotesComponent
import com.lebartodev.lnote.di.app.AppModule
import com.lebartodev.lnote.di.notes.NotesModule
import com.lebartodev.lnote.repository.NotesDAOTest
import com.lebartodev.lnote.utils.di.notes.NotesComponentTest
import com.lebartodev.lnote.utils.mocks.LNoteApplicationMock
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface NotesComponentTest : NotesComponent {
    fun inject(application: LNoteApplicationMock)
    fun inject(notesDAOTest: NotesDAOTest)
    override fun plus(module: NotesModule): NotesComponentTest
}