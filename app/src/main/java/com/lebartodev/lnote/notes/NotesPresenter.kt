package com.lebartodev.lnote.notes

import com.lebartodev.lnote.base.BasePresenter
import com.lebartodev.lnote.utils.SchedulersFacade
import javax.inject.Inject


class NotesPresenter @Inject constructor(view: Notes.View, private val notesRepository: NotesRepository,
                                         private val schedulersFacade: SchedulersFacade) :
        BasePresenter<Notes.View>(view), Notes.Presenter {

    override fun loadNotes() {
        addDisposable(notesRepository.getNotes()
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .subscribe(view::onNotesLoaded, view::onLoadError)
        )
    }

}