package com.lebartodev.lnote.di.module

import android.app.Application
import androidx.room.Room
import com.lebartodev.lnote.data.AppDatabase
import com.lebartodev.lnote.utils.DebugOpenClass
import com.lebartodev.lnote.utils.SchedulersFacade
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@DebugOpenClass
@Singleton
@Module
class AppModule(private val app: Application) {
    @Provides
    @Singleton
    fun provideApp(): Application = app

    @Provides
    fun provideDatabase(application: Application): AppDatabase {
        return Room.databaseBuilder(application, AppDatabase::class.java, "database.db").build()
    }

    @Provides
    fun provideSchedulersFacade(): SchedulersFacade {
        return SchedulersFacade()
    }
}