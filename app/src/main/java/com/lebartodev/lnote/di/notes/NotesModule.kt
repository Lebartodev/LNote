package com.lebartodev.lnote.di.notes

import android.app.Application
import android.content.Context
import com.lebartodev.lnote.R
import com.lebartodev.lnote.data.AppDatabase
import com.lebartodev.lnote.repository.NotesRepository
import com.lebartodev.lnote.repository.SettingsRepository
import com.lebartodev.lnote.utils.DebugOpenClass
import com.lebartodev.lnote.utils.LNoteViewModelFactory
import com.lebartodev.lnote.utils.SchedulersFacade
import dagger.Module
import dagger.Provides

@DebugOpenClass
@Module
class NotesModule {
    @Provides
    fun provideSettingsRepository(app: Application): SettingsRepository = SettingsRepository(app.getSharedPreferences(app.getString(R.string.settings_tag), Context.MODE_PRIVATE))

    @Provides
    fun provideNotesRepository(database: AppDatabase, schedulersFacade: SchedulersFacade): NotesRepository = NotesRepository(database, schedulersFacade)

    @Provides
    fun provideLNotesViewModelFactory(notesRepository: NotesRepository, settingsRepository: SettingsRepository, schedulersFacade: SchedulersFacade): LNoteViewModelFactory {
        return LNoteViewModelFactory(notesRepository, settingsRepository, schedulersFacade)
    }
}