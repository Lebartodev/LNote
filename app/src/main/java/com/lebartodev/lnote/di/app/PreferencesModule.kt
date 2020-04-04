package com.lebartodev.lnote.di.app

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.lebartodev.lnote.R
import dagger.Module
import dagger.Provides

@Module
class PreferencesModule {
    @Provides
    fun providePreferences(application: Application): SharedPreferences = application.getSharedPreferences(application.getString(R.string.settings_tag), Context.MODE_PRIVATE)
}