package com.lebartodev.core.di.app

import android.app.Application
import com.lebartodev.core.data.Manager
import com.lebartodev.core.data.repository.Repository
import com.lebartodev.core.db.AppDatabase
import com.lebartodev.core.di.AppModule
import com.lebartodev.core.di.utils.AppScope
import com.lebartodev.core.utils.SchedulersFacade
import dagger.BindsInstance
import dagger.Component

@AppScope
@Component(modules = [AppModule::class])
interface AppComponent {
    fun appDatabase(): AppDatabase
    fun schedulersFacade(): SchedulersFacade
    fun settingsManager(): Repository.Settings
    fun notesRepository(): Repository.Notes

    @Component.Builder
    interface Builder {
        fun build(): AppComponent

        @BindsInstance
        fun applicationContext(application: Application): Builder
    }
}