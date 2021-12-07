package com.lebartodev.lnotes.list.di

import com.lebartodev.core.di.app.CoreComponent
import com.lebartodev.core.di.utils.FeatureScope
import com.lebartodev.lnotes.list.NotesFragment
import dagger.Component

@FeatureScope
@Component(dependencies = [CoreComponent::class], modules = [ListModule::class])
interface ListComponent {
    fun inject(notesFragment: NotesFragment)
}