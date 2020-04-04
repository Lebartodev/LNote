package com.lebartodev.lnote.di.app

import com.lebartodev.lnote.data.CurrentNoteManager
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface ManagersModule {

    @Binds
    @Singleton
    fun provideCurrentNoteManager(currentNoteManager: CurrentNoteManager): CurrentNoteManager
}