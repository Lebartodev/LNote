package com.lebartodev.lnote.common

import android.app.Application
import com.lebartodev.core.di.app.AppComponent
import com.lebartodev.core.di.app.DaggerAppComponent
import com.lebartodev.core.di.utils.AppComponentProvider

open class LNoteApplication : Application(), AppComponentProvider {

    var appComponent: AppComponent? = null

    override fun onCreate() {
        super.onCreate()
        appComponent = createAppComponent()
    }

    open fun createAppComponent() = DaggerAppComponent.builder()
            .applicationContext(this)
            .build()

    override fun onTerminate() {
        super.onTerminate()
        appComponent = null
    }

    override fun provideAppComponent(): AppComponent = appComponent!!
}