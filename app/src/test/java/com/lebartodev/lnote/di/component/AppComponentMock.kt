package com.lebartodev.lnote.di.component

import android.app.Application
import com.lebartodev.lnote.di.module.AppModule
import com.lebartodev.lnote.di.module.NotesModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponentMock : AppComponent {
    fun inject(application: Application)
    override fun plus(module: NotesModule): NotesComponentMock
}