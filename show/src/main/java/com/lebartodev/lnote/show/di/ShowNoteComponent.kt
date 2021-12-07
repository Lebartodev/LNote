package com.lebartodev.lnote.show.di

import android.content.Context
import com.lebartodev.core.di.app.CoreComponent
import com.lebartodev.lnote.show.ShowNoteFragment
import dagger.BindsInstance
import dagger.Component

@ShowNoteScope
@Component(dependencies = [CoreComponent::class], modules = [ShowNoteModule::class])
interface ShowNoteComponent {
    fun inject(view: ShowNoteFragment)

    @Component.Builder
    interface Builder {
        fun build(): ShowNoteComponent

        @BindsInstance
        fun context(context: Context): Builder

        fun appComponent(coreComponent: CoreComponent): Builder
    }
}