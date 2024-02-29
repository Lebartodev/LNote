package com.lebartodev.core.di.app

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.lebartodev.core.R
import com.lebartodev.core.data.CurrentNoteManager
import com.lebartodev.core.data.Manager
import com.lebartodev.core.data.repository.NotesRepository
import com.lebartodev.core.data.repository.Repository
import com.lebartodev.core.data.repository.SettingsRepository
import com.lebartodev.core.db.AppDatabase
import com.lebartodev.core.di.utils.AppScope
import com.lebartodev.core.di.utils.ViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module(includes = [FactoryModule::class])
class CoreModule {
    @Provides
    @AppScope
    fun provideDatabase(application: Application): AppDatabase =
        Room.databaseBuilder(application, AppDatabase::class.java, "database.db").build()

    @Provides
    @AppScope
    fun providePreferences(application: Application): SharedPreferences =
        application.getSharedPreferences(
            application.getString(R.string.settings_tag),
            Context.MODE_PRIVATE
        )

    @Provides
    @AppScope
    fun provideAppContext(application: Application): Context = application.applicationContext

    @Provides
    @AppScope
    fun provideSettingsRepository(settingsRepository: SettingsRepository): Repository.Settings =
        settingsRepository

    @Provides
    @AppScope
    fun provideNotesRepository(notesRepository: NotesRepository): Repository.Notes = notesRepository

    @Provides
    @AppScope
    fun provideCurrentNoteManager(currentNoteManager: CurrentNoteManager): Manager.CurrentNote =
        currentNoteManager
}

@Module
interface FactoryModule {
    @Binds
    fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}