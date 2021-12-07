package com.lebartodev.lnote.common

import android.app.Application
import com.lebartodev.core.di.app.CoreComponent
import com.lebartodev.core.di.app.DaggerCoreComponent
import com.lebartodev.core.di.utils.CoreComponentProvider

open class LNoteApplication : Application(), CoreComponentProvider {

    override lateinit var coreComponent: CoreComponent

    override fun onCreate() {
        super.onCreate()
        coreComponent = DaggerCoreComponent.builder()
            .applicationContext(this)
            .build()
    }
}