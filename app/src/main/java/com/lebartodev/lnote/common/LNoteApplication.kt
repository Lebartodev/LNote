package com.lebartodev.lnote.common

import android.app.Application
import android.content.Context
import com.lebartodev.lnote.di.app.AppComponent
import com.lebartodev.lnote.di.app.DaggerAppComponent
import com.lebartodev.lnote.di.notes.NotesComponent


open class LNoteApplication : Application() {
    companion object {
        operator fun get(context: Context): LNoteApplication {
            return context.applicationContext as LNoteApplication
        }
    }

    lateinit var appComponent: AppComponent
        private set
    var notesComponent: NotesComponent? = null

    override fun onCreate() {
        super.onCreate()
        appComponent = createAppComponent()
    }

    open fun createAppComponent() = DaggerAppComponent.builder()
            .applicationContext(this)
            .build()
}