package com.lebartodev.lnote.utils.di.app

import android.app.Application
import com.lebartodev.lnote.data.AppDatabase
import com.nhaarman.mockitokotlin2.mock
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModuleMock {
    @Provides
    @Singleton
    fun provideDatabaseMock(application: Application): AppDatabase = mock()
}