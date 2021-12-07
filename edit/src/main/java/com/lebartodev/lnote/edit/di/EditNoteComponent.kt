package com.lebartodev.lnote.edit.di

import com.lebartodev.core.di.app.CoreComponent
import com.lebartodev.core.di.utils.FeatureScope
import com.lebartodev.lnote.edit.EditNoteFragment
import com.lebartodev.lnote.edit.creation.NoteCreationView
import dagger.Component

@FeatureScope
@Component(dependencies = [CoreComponent::class], modules = [EditNoteModule::class])
interface EditNoteComponent {
    fun inject(editNoteFragment: EditNoteFragment)
    fun inject(noteCreationView: NoteCreationView)
}