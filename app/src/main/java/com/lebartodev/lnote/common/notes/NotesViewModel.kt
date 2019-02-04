package com.lebartodev.lnote.common.notes

import androidx.lifecycle.*
import com.lebartodev.lnote.data.entity.Note
import com.lebartodev.lnote.data.entity.ViewModelObject
import com.lebartodev.lnote.repository.NotesRepository
import com.lebartodev.lnote.utils.DebugOpenClass
import java.text.SimpleDateFormat
import java.util.*

@DebugOpenClass
class NotesViewModel constructor(var notesRepository: NotesRepository) : ViewModel(), NotesScreen.ViewModel {
    var selectedDate = MutableLiveData<Calendar?>()

    fun setDate(year: Int, month: Int, day: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)
        selectedDate.postValue(calendar)
    }

    fun clearDate() {
        selectedDate.postValue(null)
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

    fun saveNote(title: String?, text: String?): LiveData<ViewModelObject<Long>> =
            LiveDataReactiveStreams.fromPublisher(
                    notesRepository.createNote(title, text, selectedDate.value?.timeInMillis).toFlowable()
                            .map { ViewModelObject.success(it) }
                            .onErrorReturn { ViewModelObject.error(it, null) })

    fun loadNotes(): LiveData<ViewModelObject<List<Note>>> {
        return LiveDataReactiveStreams.fromPublisher(notesRepository.getNotes()
                .map { ViewModelObject.success(it) }
                .onErrorReturn {
                    ViewModelObject.error(it, arrayListOf())
                })
    }
}