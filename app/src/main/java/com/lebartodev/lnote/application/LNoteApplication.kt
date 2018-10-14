package com.lebartodev.lnote.application

import android.app.Application
import android.content.Context
import com.lebartodev.lnote.application.component.AppComponent
import com.lebartodev.lnote.application.component.DaggerAppComponent
import com.lebartodev.lnote.application.module.AppModule

class LNoteApplication : Application() {
    private lateinit var component: AppComponent


    override fun onCreate() {
        super.onCreate()
        setupGraph()
    }

    private fun setupGraph() {
        component = DaggerAppComponent.builder().appModule(AppModule(this)).build()
        component.inject(this)
    }

    fun component(): AppComponent? {
        return component
    }

    companion object {
        operator fun get(context: Context): LNoteApplication {
            return context.applicationContext as LNoteApplication
        }
    }
}