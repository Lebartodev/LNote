package com.lebartodev.lnote.common.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lebartodev.lnote.data.entity.Note
import com.lebartodev.lnote.repository.NotesRepository
import com.lebartodev.lnote.utils.DebugOpenClass
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.internal.functions.Functions
import io.reactivex.schedulers.Schedulers

@DebugOpenClass
class NoteDetailsViewModel constructor(var notesRepository: NotesRepository) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()
    val noteDetailsLiveData = MutableLiveData<Note?>()

    fun getDetails(): LiveData<Note?> = noteDetailsLiveData

    fun fetchDetails(id: Long) {
        compositeDisposable.add(
                notesRepository.getNoteDetails(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(Consumer { noteDetailsLiveData.postValue(it) }, Functions.emptyConsumer()))
    }
}