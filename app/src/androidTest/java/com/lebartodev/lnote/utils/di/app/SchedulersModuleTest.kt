package com.lebartodev.lnote.utils.di.app

import com.lebartodev.core.utils.SchedulersFacade
import com.lebartodev.lnote.utils.mocks.SchedulersFacadeMock
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface SchedulersModuleTest {
    @Binds
    @Singleton
    fun provideSchedulersFacade(applicationContext: SchedulersFacadeMock): SchedulersFacade
}