package com.lebartodev.lnote.archive

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lebartodev.core.base.BaseViewModel
import com.lebartodev.core.data.repository.Repository
import com.lebartodev.core.db.entity.Note
import com.lebartodev.core.utils.SchedulersFacade
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.internal.functions.Functions
import io.reactivex.internal.functions.Functions.EMPTY_ACTION

class ArchiveViewModel constructor(var notesRepository: Repository.Notes,
                                   val schedulersFacade: SchedulersFacade) : BaseViewModel() {
    private var notesDisposable = CompositeDisposable()

    private val notesLiveData: MutableLiveData<List<Note>> = MutableLiveData()

    fun getNotes(): LiveData<List<Note>> = notesLiveData

    init {
        notesDisposable.add(notesRepository.getArchive()
            .subscribeOn(schedulersFacade.io())
            .observeOn(schedulersFacade.ui())
            .subscribe({ notesLiveData.value = it },
                Functions.emptyConsumer()))
    }

    fun deleteNote(id: Long) {
        notesDisposable.add(notesRepository.completleDeleteNote(id)
            .subscribeOn(schedulersFacade.io())
            .observeOn(schedulersFacade.ui())
            .subscribe(EMPTY_ACTION,
                Functions.emptyConsumer()))
    }

    fun restoreNote(id: Long) {
        notesDisposable.add(notesRepository.restoreNote(id)
            .subscribeOn(schedulersFacade.io())
            .observeOn(schedulersFacade.ui())
            .subscribe(EMPTY_ACTION,
                Functions.emptyConsumer()))
    }

    override fun onCleared() {
        super.onCleared()
        notesDisposable.dispose()
    }


}