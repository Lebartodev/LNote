package com.lebartodev.lnote.utils.di.component

import com.lebartodev.lnote.di.component.AppComponent
import com.lebartodev.lnote.di.module.AppModule
import com.lebartodev.lnote.di.module.NotesModule
import com.lebartodev.lnote.utils.mocks.LNoteApplicationMock
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponentTest : AppComponent {
    fun inject(application: LNoteApplicationMock)
    override fun plus(module: NotesModule): NotesComponentTest
}