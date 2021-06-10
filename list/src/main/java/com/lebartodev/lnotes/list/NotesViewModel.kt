package com.lebartodev.lnotes.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lebartodev.core.base.BaseViewModel
import com.lebartodev.core.data.repository.Repository
import com.lebartodev.core.db.entity.Note
import com.lebartodev.core.utils.SchedulersFacade
import com.lebartodev.lnote.utils.exception.DeleteNoteException
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.internal.functions.Functions

class NotesViewModel constructor(var notesRepository: Repository.Notes, val schedulersFacade: SchedulersFacade) : BaseViewModel() {
    private var notesDisposable = CompositeDisposable()

    private val notesLiveData: MutableLiveData<List<Note>> = MutableLiveData()

    fun getNotes(): LiveData<List<Note>> = notesLiveData

    init {
        fetchNotes()
    }

    fun fetchNotes() {
        notesDisposable.dispose()
        notesDisposable.add(notesRepository.getNotes()
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .subscribe({ notesLiveData.value = it },
                        Functions.emptyConsumer()))
    }

    override fun onCleared() {
        super.onCleared()
        notesDisposable.dispose()
    }

    fun deleteNote(id: Long) {
        notesDisposable.add(
                notesRepository.deleteNote(id)
                        .subscribeOn(schedulersFacade.io())
                        .observeOn(schedulersFacade.ui())
                        .subscribe({
                            //TODO:  deleteResultLiveData.value = true
                        }, { postError(DeleteNoteException(it)) })
        )
    }


}