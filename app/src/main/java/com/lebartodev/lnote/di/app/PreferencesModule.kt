package com.lebartodev.lnote.di.app

import android.content.Context
import android.content.SharedPreferences
import com.lebartodev.lnote.R
import dagger.Module
import dagger.Provides

@Module
class PreferencesModule {
    @Provides
    fun providePreferences(context: Context): SharedPreferences = context.getSharedPreferences(context.getString(R.string.settings_tag), Context.MODE_PRIVATE)
}