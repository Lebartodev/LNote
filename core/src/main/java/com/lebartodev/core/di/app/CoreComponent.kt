package com.lebartodev.core.di.app

import android.app.Application
import android.content.Context
import com.lebartodev.core.data.repository.Repository
import com.lebartodev.core.db.AppDatabase
import com.lebartodev.core.di.utils.AppScope
import com.lebartodev.core.utils.SchedulersFacade
import dagger.BindsInstance
import dagger.Component

@AppScope
@Component(modules = [CoreModule::class])
interface CoreComponent {
    fun appDatabase(): AppDatabase
    fun schedulersFacade(): SchedulersFacade
    fun settingsManager(): Repository.Settings
    fun notesRepository(): Repository.Notes
    fun appContext(): Context

    @Component.Builder
    interface Builder {
        fun build(): CoreComponent

        @BindsInstance
        fun applicationContext(application: Application): Builder
    }
}