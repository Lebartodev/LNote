package com.lebartodev.lnote.application.component

import com.lebartodev.lnote.application.LNoteApplication
import com.lebartodev.lnote.application.module.AppModule
import com.lebartodev.lnote.application.module.NotesModule
import com.lebartodev.lnote.notes.NotesRepository
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, NotesModule::class])
interface AppComponent {
    fun inject(categoryApplication: LNoteApplication)

    fun getNotesRepository(): NotesRepository
}