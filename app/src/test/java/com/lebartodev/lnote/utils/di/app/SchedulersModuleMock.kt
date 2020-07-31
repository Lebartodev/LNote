package com.lebartodev.lnote.utils.di.app

import com.lebartodev.lnote.utils.SchedulersFacade
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface SchedulersModuleMock {
    @Binds
    @Singleton
    fun provideSchedulersFacade(applicationContext: SchedulersFacadeMock1): SchedulersFacade
}