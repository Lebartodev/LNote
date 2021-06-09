package com.lebartodev.core.di.utils

import com.lebartodev.core.di.app.AppComponent

interface AppComponentProvider {
    fun provideAppComponent(): AppComponent
}