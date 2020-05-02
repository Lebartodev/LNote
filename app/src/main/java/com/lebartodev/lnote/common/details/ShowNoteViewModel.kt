package com.lebartodev.lnote.common.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lebartodev.lnote.base.BaseViewModel
import com.lebartodev.lnote.data.Manager
import com.lebartodev.lnote.data.entity.Note
import com.lebartodev.lnote.repository.NotesRepository
import com.lebartodev.lnote.utils.SchedulersFacade
import com.lebartodev.lnote.utils.SingleLiveEvent
import io.reactivex.disposables.Disposables

class ShowNoteViewModel constructor(private val notesRepository: NotesRepository,
                                    private val schedulersFacade: SchedulersFacade,
                                    private val currentNoteManager: Manager.CurrentNote) : BaseViewModel() {
    private var disposable = Disposables.empty()
    private var deleteDisposable = Disposables.empty()
    private val currentNote = MutableLiveData<Note>()
    private val deleteResultLiveData = SingleLiveEvent<Boolean>()

    fun loadNote(id: Long) {
        disposable = notesRepository.getNote(id)
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .subscribe({ currentNote.value = it }, { postError(LoadNoteException(it)) })
    }

    fun delete() {
        currentNote.value?.run {
            currentNoteManager.setCurrentNote(this)
            currentNoteManager.deleteCurrentNote()
        }
        currentNote.value?.id?.run {
            deleteDisposable.dispose()
            deleteDisposable = notesRepository.deleteNote(this)
                    .subscribeOn(schedulersFacade.io())
                    .observeOn(schedulersFacade.ui())
                    .subscribe({ deleteResultLiveData.value = true }, { postError(DeleteNoteException(it)) })
        }
    }

    fun note(): LiveData<Note> {
        return currentNote
    }

    fun deleteResult(): LiveData<Boolean> {
        return deleteResultLiveData
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
        deleteDisposable.dispose()
    }

    class LoadNoteException(val source: Throwable) : Throwable()
    class DeleteNoteException(val source: Throwable) : Throwable()
}