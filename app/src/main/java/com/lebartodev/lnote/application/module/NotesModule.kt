package com.lebartodev.lnote.application.module

import com.lebartodev.lnote.notes.Notes
import com.lebartodev.lnote.notes.NotesPresenter
import com.lebartodev.lnote.notes.NotesRepository
import com.lebartodev.lnote.utils.SchedulersFacade
import dagger.Module
import dagger.Provides


@Module
class NotesModule(view: Notes.View) {

    val view: Notes.View = view
        @Provides
        get

    @Provides
    fun providePresenter(categoryView: Notes.View, notesRepository: NotesRepository): Notes.Presenter {
        return NotesPresenter(categoryView, notesRepository,
                SchedulersFacade())
    }
}