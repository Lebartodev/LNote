package com.lebartodev.lnote.di.component

import com.lebartodev.lnote.common.LNoteApplication
import com.lebartodev.lnote.di.module.AppModule
import com.lebartodev.lnote.di.module.NotesModule
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun plus(module: NotesModule): NotesComponent
    fun inject(application: LNoteApplication)
}