package com.lebartodev.lnote.common.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.lebartodev.lnote.data.entity.Note
import com.lebartodev.lnote.data.entity.ViewModelObject
import com.lebartodev.lnote.repository.NoteContainer
import com.lebartodev.lnote.repository.NotesRepository
import com.lebartodev.lnote.utils.DebugOpenClass
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.internal.functions.Functions
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

@DebugOpenClass
class NoteEditViewModel constructor(var notesRepository: NotesRepository) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()
    private val saveResultLiveData: MutableLiveData<ViewModelObject<Long>> = MutableLiveData()
    val selectedDate = MutableLiveData<Calendar?>()
    val descriptionTextLiveData = MutableLiveData<String?>()

    fun setDate(year: Int, month: Int, day: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)
        selectedDate.postValue(calendar)
        NoteContainer.currentNote.date = calendar.timeInMillis
    }

    fun clearDate() {
        selectedDate.postValue(null)
        NoteContainer.currentNote.date = null
    }

    fun selectedDateString(): LiveData<String> {
        return Transformations.map(selectedDate) {
            if (it == null) {
                ""
            } else {
                val formatter = SimpleDateFormat("EEE, dd MMM yyyy", Locale.US)
                formatter.format(it.time)
            }
        }
    }

    fun getSaveResult(): LiveData<ViewModelObject<Long>> = saveResultLiveData

    fun saveNote(title: String?, text: String?) {
        compositeDisposable.add(notesRepository.createNote(title, text, selectedDate.value?.timeInMillis)
                .map { ViewModelObject.success(it) }
                .onErrorReturn { ViewModelObject.error(it, null) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Consumer { saveResultLiveData.postValue(it) }, Functions.emptyConsumer()))
    }

    fun onDescriptionChanged(text: String?) {
        if (text != null && text.length > MAX_TITLE_CHARACTERS)
            descriptionTextLiveData.postValue(text.substring(0, MAX_TITLE_CHARACTERS))
        else {
            descriptionTextLiveData.postValue(text)
        }
    }

    val noteDetailsLiveData = MutableLiveData<Note?>()

    fun getDetails(): LiveData<Note?> = noteDetailsLiveData

    fun fetchDetails(id: Long) {
        compositeDisposable.add(
                notesRepository.getNote(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(Consumer { noteDetailsLiveData.postValue(it) }, Functions.emptyConsumer()))
    }

    companion object {
        private const val MAX_TITLE_CHARACTERS = 24
    }
}