package com.lebartodev.lnote.utils.di.notes

import android.content.Context
import com.lebartodev.lnote.common.details.EditNoteFragmentTest
import com.lebartodev.lnote.common.notes.NotesActivity
import com.lebartodev.lnote.common.notes.NotesActivityInstrumentationTest
import com.lebartodev.lnote.di.app.AppComponent
import com.lebartodev.lnote.di.utils.NotesScope
import com.lebartodev.lnote.utils.di.app.AppComponentTest
import dagger.BindsInstance
import dagger.Component

@NotesScope
@Component(dependencies = [AppComponentTest::class], modules = [NotesModuleTest::class])
interface NotesComponentTest {
    fun inject(notesFragment: EditNoteFragmentTest)
    fun inject(notesActivity: NotesActivityInstrumentationTest)

    @Component.Builder
    interface Builder {
        fun build(): NotesComponentTest
        @BindsInstance
        fun context(context: Context): Builder

        fun appComponent(appComponent: AppComponentTest): Builder
    }
}