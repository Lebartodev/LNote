package com.lebartodev.lnote.common.notes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lebartodev.lnote.data.entity.Note
import com.lebartodev.lnote.data.entity.ViewModelObject
import com.lebartodev.lnote.repository.NotesRepository
import com.lebartodev.lnote.utils.DebugOpenClass
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.internal.functions.Functions
import io.reactivex.schedulers.Schedulers
import java.util.*

@DebugOpenClass
class NotesViewModel constructor(var notesRepository: NotesRepository) :
        ViewModel(), NotesScreen.ViewModel {
    private val compositeDisposable = CompositeDisposable()
    private val notesLiveData: MutableLiveData<ViewModelObject<List<Note>>> = MutableLiveData()
    var selectedDate = MutableLiveData<Calendar?>()
    val descriptionTextLiveData = MutableLiveData<String?>()

    fun getNotes(): LiveData<ViewModelObject<List<Note>>> = notesLiveData

    fun fetchNotes() {
        compositeDisposable.add(
                notesRepository.getNotes()
                        .map { ViewModelObject.success(it) }
                        .onErrorReturn { ViewModelObject.error(it, arrayListOf()) }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(Consumer { notesLiveData.postValue(it) },
                                Functions.emptyConsumer()))
    }
}