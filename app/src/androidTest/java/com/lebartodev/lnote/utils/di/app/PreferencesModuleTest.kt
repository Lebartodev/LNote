package com.lebartodev.lnote.utils.di.app

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.lebartodev.lnote.R
import dagger.Module
import dagger.Provides

@Module
class PreferencesModuleTest {
    @Provides
    fun providePreferences(application: Application): SharedPreferences = application.getSharedPreferences(application.getString(R.string.settings_tag), Context.MODE_PRIVATE)
}