package com.lebartodev.lnotes.list.di

import android.content.Context
import com.lebartodev.core.di.app.AppComponent
import com.lebartodev.lnotes.list.NotesFragment
import dagger.BindsInstance
import dagger.Component

@ListScope
@Component(dependencies = [AppComponent::class], modules = [ListModule::class])
interface ListComponent {
    fun inject(notesFragment: NotesFragment)

    @Component.Builder
    interface Builder {
        fun build(): ListComponent

        @BindsInstance
        fun context(context: Context): Builder

        fun appComponent(appComponent: AppComponent): Builder
    }
}