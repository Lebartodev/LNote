package com.lebartodev.lnote.di.app

import android.app.Application
import com.lebartodev.lnote.data.AppDatabase
import com.lebartodev.lnote.utils.SchedulersFacadeImpl
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.schedulers.Schedulers

class AppModuleMock(application: Application) : AppModule(application) {
    override fun provideDatabase(application: Application): AppDatabase = mock()
    override fun provideSchedulersFacade(): SchedulersFacadeImpl {
        val schedulersFacade: SchedulersFacadeImpl = mock()
        whenever(schedulersFacade.io()).thenReturn(Schedulers.trampoline())
        whenever(schedulersFacade.ui()).thenReturn(Schedulers.trampoline())
        return schedulersFacade
    }
}