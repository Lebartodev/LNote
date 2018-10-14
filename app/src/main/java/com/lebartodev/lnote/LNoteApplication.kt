package com.lebartodev.lnote

import android.app.Application
import android.content.Context
import com.lebartodev.lnote.di.component.AppComponent
import com.lebartodev.lnote.di.component.DaggerAppComponent


class LNoteApplication : Application() {
    private lateinit var component: AppComponent

    companion object {
        operator fun get(context: Context): LNoteApplication {
            return context.applicationContext as LNoteApplication
        }
    }

    override fun onCreate() {
        super.onCreate()
        setupGraph()
    }

    private fun setupGraph() {
        component = DaggerAppComponent.builder()
                .withApplication(this)
                .build()
    }

    fun component(): AppComponent {
        return component
    }
}