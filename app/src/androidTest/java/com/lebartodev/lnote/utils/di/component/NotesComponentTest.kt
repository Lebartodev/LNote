package com.lebartodev.lnote.utils.di.component

import com.lebartodev.lnote.common.notes.NotesActivity
import com.lebartodev.lnote.common.notes.NotesActivityInstrumentationTest
import com.lebartodev.lnote.common.repository.NotesDAOTest
import com.lebartodev.lnote.di.component.NotesComponent
import com.lebartodev.lnote.di.module.NotesModule
import dagger.Subcomponent

@Subcomponent(modules = [NotesModule::class])
interface NotesComponentTest : NotesComponent {
    override fun inject(activity: NotesActivity)
    fun inject(notesDAOTest: NotesDAOTest)
    fun inject(notesActivityInstrumentationTest: NotesActivityInstrumentationTest)
}