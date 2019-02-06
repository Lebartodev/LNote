package com.lebartodev.lnote.di.component

import com.lebartodev.lnote.common.notes.NotesActivity
import com.lebartodev.lnote.di.module.NotesModule
import dagger.Subcomponent

@Subcomponent(modules = [NotesModule::class])
interface NotesComponent {
    fun inject(activity: NotesActivity)
}