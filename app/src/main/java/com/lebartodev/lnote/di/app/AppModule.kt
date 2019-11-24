package com.lebartodev.lnote.di.app

import android.app.Application
import androidx.room.Room
import com.lebartodev.lnote.data.AppDatabase
import com.lebartodev.lnote.utils.DebugOpenClass
import com.lebartodev.lnote.utils.SchedulersFacade
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@DebugOpenClass
@Module
class AppModule(private val app: Application) {
    @Provides
    @Singleton
    fun provideApp(): Application = app

    @Provides
    @Singleton
    fun provideDatabase(application: Application): AppDatabase = Room.databaseBuilder(application, AppDatabase::class.java, "database.db").build()

    @Provides
    @Singleton
    fun provideSchedulersFacade(): SchedulersFacade = SchedulersFacade()
}