package com.lebartodev.lnote.di.module

import com.lebartodev.lnote.data.AppDatabase
import com.lebartodev.lnote.utils.SchedulersFacade
import dagger.Module
import dagger.Provides
import io.mockk.mockk
import javax.inject.Singleton

@Singleton
@Module
class AppModuleMock {
    @Provides
    @Singleton
    fun provideDatabase(): AppDatabase = mockk()
    @Provides
    @Singleton
    fun provideSchedulersFacade(): SchedulersFacade = mockk()

}