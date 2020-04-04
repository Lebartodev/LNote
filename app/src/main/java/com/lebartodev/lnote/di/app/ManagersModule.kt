package com.lebartodev.lnote.di.app

import com.lebartodev.lnote.data.CurrentNoteManager
import com.lebartodev.lnote.data.Manager
import com.lebartodev.lnote.data.SettingsManager
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface ManagersModule {
    @Binds
    @Singleton
    fun provideCurrentNoteManager(currentNoteManager: CurrentNoteManager): Manager.CurrentNote

    @Binds
    @Singleton
    fun provideSettingsManager(settingsManager: SettingsManager): Manager.Settings
}