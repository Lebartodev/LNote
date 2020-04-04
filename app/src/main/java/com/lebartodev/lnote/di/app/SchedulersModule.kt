package com.lebartodev.lnote.di.app

import com.lebartodev.lnote.utils.SchedulersFacade
import com.lebartodev.lnote.utils.SchedulersFacadeImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface SchedulersModule {
    @Binds
    @Singleton
    fun provideSchedulersFacade(applicationContext: SchedulersFacadeImpl): SchedulersFacade
}