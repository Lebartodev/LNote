package com.lebartodev.lnote.di.app

import com.lebartodev.lnote.common.LNoteApplication
import com.lebartodev.lnote.di.notes.NotesComponent
import com.lebartodev.lnote.di.notes.NotesModule
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun plus(module: NotesModule): NotesComponent
    fun inject(application: LNoteApplication)
}