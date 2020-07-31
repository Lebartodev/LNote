package com.lebartodev.lnote.utils.di.app

import com.lebartodev.lnote.di.app.SchedulersModule
import com.lebartodev.lnote.utils.SchedulersFacade
import com.lebartodev.lnote.utils.SchedulersFacadeImpl
import com.lebartodev.lnote.utils.mocks.SchedulersFacadeMock
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
interface SchedulersModuleTest {
    @Binds
    @Singleton
    fun provideSchedulersFacade(applicationContext: SchedulersFacadeMock): SchedulersFacade
}