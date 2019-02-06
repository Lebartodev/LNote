package com.lebartodev.lnote.common

import android.app.Application
import android.content.Context
import com.lebartodev.lnote.di.component.AppComponent
import com.lebartodev.lnote.di.component.DaggerAppComponent
import com.lebartodev.lnote.di.module.AppModule


open class LNoteApplication : Application() {
    open lateinit var component: AppComponent

    companion object {
        operator fun get(context: Context): LNoteApplication {
            return context.applicationContext as LNoteApplication
        }
    }

    override fun onCreate() {
        super.onCreate()
        setupGraph()
    }

    open fun setupGraph() {
        component = DaggerAppComponent.builder().appModule(AppModule(this)).build()
        component.inject(this)
    }

    open fun component(): AppComponent {
        return component
    }
}