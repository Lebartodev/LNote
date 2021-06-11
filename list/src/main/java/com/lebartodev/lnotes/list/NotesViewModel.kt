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

class NotesViewModel constructor(
    private val settingsRepository: Repository.Settings,
    private val notesRepository: Repository.Notes,
    private val schedulersFacade: SchedulersFacade
) :
    BaseViewModel() {
    private var notesDisposable = CompositeDisposable()

    private val notesLiveData: MutableLiveData<List<Note>> = MutableLiveData()
    private val bottomPanelEnabledLiveData = MutableLiveData<Boolean?>()

    fun getNotes(): LiveData<List<Note>> = notesLiveData
    fun bottomPanelEnabled(): LiveData<Boolean?> = bottomPanelEnabledLiveData

    init {
        notesDisposable.add(
            settingsRepository.bottomPanelEnabled()
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .subscribe(
                    { bottomPanelEnabledLiveData.value = it },
                    Functions.emptyConsumer()
                )
        )
        fetchNotes()
    }

    fun fetchNotes() {
        notesDisposable.add(
            notesRepository.getNotes()
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .subscribe(
                    { notesLiveData.value = it },
                    Functions.emptyConsumer()
                )
        )
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