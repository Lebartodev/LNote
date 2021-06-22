package com.lebartodev.core.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.lebartodev.core.R
import com.lebartodev.core.data.repository.NotesRepository
import com.lebartodev.core.data.repository.Repository
import com.lebartodev.core.data.repository.SettingsRepository
import com.lebartodev.core.db.AppDatabase
import com.lebartodev.core.di.utils.AppScope
import com.lebartodev.core.utils.SchedulersFacade
import com.lebartodev.core.utils.SchedulersFacadeImpl
import dagger.Module
import dagger.Provides

@Module
class AppModule {
    @Provides
    @AppScope
    fun provideDatabase(application: Application): AppDatabase = Room.databaseBuilder(application, AppDatabase::class.java, "database.db").build()

    @Provides
    @AppScope
    fun providePreferences(application: Application): SharedPreferences = application.getSharedPreferences(application.getString(R.string.settings_tag), Context.MODE_PRIVATE)

    @Provides
    @AppScope
    fun provideSchedulersFacade(schedulersFacade: SchedulersFacadeImpl): SchedulersFacade = schedulersFacade

    @Provides
    @AppScope
    fun provideSettingsRepository(settingsRepository: SettingsRepository): Repository.Settings = settingsRepository

    @Provides
    @AppScope
    fun provideNotesRepository(notesRepository: NotesRepository): Repository.Notes = notesRepository
}