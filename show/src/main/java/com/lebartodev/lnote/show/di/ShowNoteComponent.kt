package com.lebartodev.lnote.show.di

import android.content.Context
import com.lebartodev.core.di.app.AppComponent
import dagger.BindsInstance
import dagger.Component

@ShowNoteScope
@Component(dependencies = [AppComponent::class], modules = [ShowNoteModule::class])
interface ShowNoteComponent {
    @Component.Builder
    interface Builder {
        fun build(): ShowNoteComponent

        @BindsInstance
        fun context(context: Context): Builder

        fun appComponent(appComponent: AppComponent): Builder
    }
}