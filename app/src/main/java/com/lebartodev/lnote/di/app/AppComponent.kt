package com.lebartodev.lnote.di.app

import android.app.Application
import com.lebartodev.lnote.data.AppDatabase
import com.lebartodev.lnote.data.CurrentNoteManager
import com.lebartodev.lnote.utils.SchedulersFacade
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DatabaseModule::class, SchedulersModule::class])
interface AppComponent {
    fun appDatabase(): AppDatabase
    fun schedulersFacade(): SchedulersFacade
    fun currentNoteManager(): CurrentNoteManager

    @Component.Builder
    interface Builder {
        fun build(): AppComponent
        @BindsInstance
        fun applicationContext(application: Application): Builder
    }
}