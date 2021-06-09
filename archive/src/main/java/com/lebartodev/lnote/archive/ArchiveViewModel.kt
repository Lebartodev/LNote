package com.lebartodev.lnote.archive

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lebartodev.core.db.entity.Note
import com.lebartodev.core.base.BaseViewModel
import com.lebartodev.core.utils.SchedulersFacade
import com.lebartodev.core.data.repository.Repository

import io.reactivex.disposables.Disposables
import io.reactivex.functions.Consumer
import io.reactivex.internal.functions.Functions

class ArchiveViewModel constructor(var notesRepository: Repository.Notes, val schedulersFacade: SchedulersFacade) : BaseViewModel() {
    private var notesDisposable = Disposables.empty()

    private val notesLiveData: MutableLiveData<List<Note>> = MutableLiveData()

    fun getNotes(): LiveData<List<Note>> = notesLiveData

    init {
        fetchNotes()
    }

    fun fetchNotes() {
        notesDisposable.dispose()
        notesDisposable = notesRepository.getArchive()
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .subscribe(Consumer { notesLiveData.value = it },
                        Functions.emptyConsumer())
    }

    override fun onCleared() {
        super.onCleared()
        notesDisposable.dispose()
    }


}