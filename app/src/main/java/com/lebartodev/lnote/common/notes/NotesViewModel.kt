package com.lebartodev.lnote.common.notes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lebartodev.lnote.data.entity.Note
import com.lebartodev.lnote.data.entity.ViewModelObject
import com.lebartodev.lnote.repository.NotesRepository
import com.lebartodev.lnote.utils.DebugOpenClass
import com.lebartodev.lnote.utils.SchedulersFacade
import io.reactivex.disposables.Disposables
import io.reactivex.functions.Consumer
import io.reactivex.internal.functions.Functions

@DebugOpenClass
class NotesViewModel constructor(var notesRepository: NotesRepository, val schedulersFacade: SchedulersFacade) : ViewModel() {
    private var notesDisposable = Disposables.empty()
    private val notesLiveData: MutableLiveData<ViewModelObject<List<Note>>> = MutableLiveData()
    private val bottomSheetOpenLiveData = MutableLiveData<Boolean>().apply { value = false }

    private val noteDeleteDialogLiveData: MutableLiveData<Boolean?> = MutableLiveData()

    fun getNotes(): LiveData<ViewModelObject<List<Note>>> = notesLiveData

    fun noteDeleteDialog(): LiveData<Boolean?> = noteDeleteDialogLiveData

    fun bottomSheetOpen(): LiveData<Boolean> = bottomSheetOpenLiveData

    init {
        fetchNotes()
    }

    private fun fetchNotes() {
        notesDisposable.dispose()
        notesDisposable = notesRepository.getNotes()
                .map { ViewModelObject.success(it) }
                .onErrorReturn { ViewModelObject.error(it, arrayListOf()) }
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .subscribe(Consumer { notesLiveData.value = it },
                        Functions.emptyConsumer())
    }

    fun onNoteDeleted() {
        noteDeleteDialogLiveData.value = true
    }

    fun toggleBottomSheet() {
        bottomSheetOpenLiveData.value = !(bottomSheetOpenLiveData.value ?: false)
    }

    override fun onCleared() {
        super.onCleared()
        notesDisposable.dispose()
    }


}