package com.lebartodev.lnotes.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lebartodev.core.base.BaseViewModel
import com.lebartodev.core.data.NoteData
import com.lebartodev.core.data.repository.Repository
import com.lebartodev.core.db.entity.Note
import com.lebartodev.core.utils.SchedulersFacade
import com.lebartodev.lnote.utils.SingleLiveEvent
import com.lebartodev.lnote.utils.exception.DeleteNoteException
import com.lebartodev.lnote.utils.exception.RestoreNoteException
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
    private val deletedNoteEvent = SingleLiveEvent<Boolean>()
    private val restoredNoteEvent = SingleLiveEvent<NoteData>()

    fun getNotes(): LiveData<List<Note>> = notesLiveData
    fun getRestoredNoteEvent(): SingleLiveEvent<NoteData> = restoredNoteEvent
    fun getDeletedNoteEvent(): SingleLiveEvent<Boolean> = deletedNoteEvent
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
                    deletedNoteEvent.value = true
                }, { postError(DeleteNoteException(it)) })
        )
    }

    fun restoreLastNote() {
        notesDisposable.add(
            notesRepository.restoreLastNote()
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .subscribe({
                    if (it.created == null) {
                        restoredNoteEvent.value = NoteData(it.id, it.title, it.date, it.text, it.created)
                    }
                }, { postError(RestoreNoteException(it)) })
        )
    }
}