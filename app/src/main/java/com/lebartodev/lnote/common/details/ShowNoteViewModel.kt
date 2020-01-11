package com.lebartodev.lnote.common.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lebartodev.lnote.data.entity.Note
import com.lebartodev.lnote.data.entity.ViewModelObject
import com.lebartodev.lnote.repository.NotesRepository
import com.lebartodev.lnote.utils.DebugOpenClass
import com.lebartodev.lnote.utils.SchedulersFacade
import io.reactivex.disposables.Disposables

@DebugOpenClass
class ShowNoteViewModel constructor(private val notesRepository: NotesRepository,
                                    private val schedulersFacade: SchedulersFacade) : ViewModel() {
    private var disposable = Disposables.empty()
    private val currentNote = MutableLiveData<ViewModelObject<Note>>()

    fun loadNote(id: Long) {
        disposable = notesRepository.getNote(id)
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .subscribe({
                    currentNote.value = ViewModelObject.success(it)
                }, {
                    currentNote.value = ViewModelObject.error(it, null)
                })
    }

    fun note(): LiveData<ViewModelObject<Note>> {
        return currentNote
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}