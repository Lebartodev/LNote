package com.lebartodev.lnote.show

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lebartodev.core.base.BaseViewModel
import com.lebartodev.core.data.repository.Repository
import com.lebartodev.core.db.entity.Note
import com.lebartodev.core.utils.SchedulersFacade
import com.lebartodev.lnote.utils.SingleLiveEvent
import com.lebartodev.lnote.utils.exception.LoadNoteException
import io.reactivex.disposables.Disposables

class ShowNoteViewModel constructor(private val notesRepository: Repository.Notes, private val schedulersFacade: SchedulersFacade) : BaseViewModel() {
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
//        currentNote.value?.id?.run {
//            deleteDisposable.dispose()
//            deleteDisposable = notesRepository.deleteNote(this)
//                    .subscribeOn(schedulersFacade.io())
//                    .observeOn(schedulersFacade.ui())
//                    .subscribe({ deleteResultLiveData.value = true }, { postError(DeleteNoteException(it)) })
//        }
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
}
