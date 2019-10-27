package com.lebartodev.lnote.utils.di.app

import com.lebartodev.lnote.di.app.AppComponent
import com.lebartodev.lnote.di.app.AppModule
import com.lebartodev.lnote.di.notes.NotesModule
import com.lebartodev.lnote.utils.di.notes.NotesComponentTest
import com.lebartodev.lnote.utils.mocks.LNoteApplicationMock
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponentTest : AppComponent {
    fun inject(application: LNoteApplicationMock)
    override fun plus(module: NotesModule): NotesComponentTest
}