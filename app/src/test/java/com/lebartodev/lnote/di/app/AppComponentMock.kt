package com.lebartodev.lnote.di.app

import android.app.Application
import com.lebartodev.lnote.di.notes.NotesComponentMock
import com.lebartodev.lnote.di.notes.NotesModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponentMock : AppComponent {
    fun inject(application: Application)
    override fun plus(module: NotesModule): NotesComponentMock
}