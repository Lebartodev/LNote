package com.lebartodev.lnote.archive.di

import android.content.Context
import com.lebartodev.core.di.app.AppComponent
import com.lebartodev.lnote.archive.ArchiveFragment
import com.lebartodev.lnote.show.ShowNoteFragment
import dagger.BindsInstance
import dagger.Component

@ArchiveScope
@Component(dependencies = [AppComponent::class], modules = [ArchiveModule::class])
interface ArchiveComponent {
    fun inject(view: ArchiveFragment)

    @Component.Builder
    interface Builder {
        fun build(): ArchiveComponent

        @BindsInstance
        fun context(context: Context): Builder

        fun appComponent(appComponent: AppComponent): Builder
    }
}