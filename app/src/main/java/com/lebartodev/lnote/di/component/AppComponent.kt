package com.lebartodev.lnote.di.component

import android.app.Application
import com.lebartodev.lnote.common.notes.NotesActivity
import com.lebartodev.lnote.di.module.AppModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun withApplication(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(mainActivity: NotesActivity)
}