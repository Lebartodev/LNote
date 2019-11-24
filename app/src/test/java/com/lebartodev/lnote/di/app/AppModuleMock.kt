package com.lebartodev.lnote.di.app

import android.app.Application
import com.lebartodev.lnote.data.AppDatabase
import com.lebartodev.lnote.utils.SchedulersFacade
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.schedulers.Schedulers

class AppModuleMock(application: Application) : AppModule(application) {
    override fun provideDatabase(application: Application): AppDatabase = mock()
    override fun provideSchedulersFacade(): SchedulersFacade {
        val schedulersFacade: SchedulersFacade = mock()
        whenever(schedulersFacade.io()).thenReturn(Schedulers.trampoline())
        whenever(schedulersFacade.ui()).thenReturn(Schedulers.trampoline())
        return schedulersFacade
    }
}