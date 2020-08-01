package com.lebartodev.lnote.utils.di.app

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.lebartodev.lnote.R
import com.nhaarman.mockitokotlin2.mock
import dagger.Module
import dagger.Provides

@Module
class PreferencesModuleMock {
    @Provides
    fun providePreferences(application: Application): SharedPreferences = mock()
}