package com.lebartodev.lnote.utils.di.module

import android.app.Application
import androidx.room.Room
import com.lebartodev.lnote.data.AppDatabase
import com.lebartodev.lnote.di.module.AppModule
import com.lebartodev.lnote.utils.SchedulersFacade
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.schedulers.Schedulers

class AppModuleTest(application: Application) : AppModule(application) {
    override fun provideDatabase(application: Application): AppDatabase = Room.inMemoryDatabaseBuilder(application, AppDatabase::class.java).allowMainThreadQueries().build()
    override fun provideSchedulersFacade(): SchedulersFacade {
        val schedulersFacade: SchedulersFacade = mock()
        whenever(schedulersFacade.io()).thenReturn(Schedulers.trampoline())
        return schedulersFacade
    }
}