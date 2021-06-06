package com.lebartodev.lnote.di.app

import android.app.Application
import com.lebartodev.core.db.AppDatabase
import com.lebartodev.core.di.DatabaseModule
import com.lebartodev.lnote.data.Manager
import com.lebartodev.lnote.utils.SchedulersFacade
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DatabaseModule::class, SchedulersModule::class, ManagersModule::class, PreferencesModule::class])
interface AppComponent {
    fun appDatabase(): AppDatabase
    fun schedulersFacade(): SchedulersFacade
    fun settingsManager(): Manager.Settings
    fun currentNoteManager(): Manager.CurrentNote

    @Component.Builder
    interface Builder {
        fun build(): AppComponent

        @BindsInstance
        fun applicationContext(application: Application): Builder
    }
}