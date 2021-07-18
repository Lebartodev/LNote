package com.lebartodev.lnote.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lebartodev.core.base.BaseViewModel
import com.lebartodev.core.data.NoteData
import com.lebartodev.core.data.repository.Repository
import com.lebartodev.core.db.entity.Photo
import com.lebartodev.lnote.utils.SingleLiveEvent
import com.lebartodev.lnote.utils.extensions.formattedHint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class NoteEditViewModel constructor(
        private val notesRepository: Repository.Notes
) : BaseViewModel() {
    private val saveResultLiveData: SingleLiveEvent<Boolean> = SingleLiveEvent()
    private val deleteResultLiveData: SingleLiveEvent<Boolean> = SingleLiveEvent()
    private val currentNoteLiveData = MutableLiveData(NoteData())

    fun currentNote(): LiveData<NoteData> = currentNoteLiveData

    fun saveResult(): LiveData<Boolean?> = saveResultLiveData

    fun deleteResult(): LiveData<Boolean?> = deleteResultLiveData

    fun loadNote(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val note = notesRepository.getNote(id).first()
            withContext(Dispatchers.Main) {
                currentNoteLiveData.value = NoteData(note.id, note.title, note.date,
                        note.text, note.created, note.photos)
            }
        }
    }

    fun setDescription(value: String) {
        currentNoteLiveData.value = currentNoteLiveData.value?.apply { text = value }
    }

    fun setTitle(value: String) {
        currentNoteLiveData.value = currentNoteLiveData.value?.apply { title = value }
    }

    fun setDate(year: Int, month: Int, day: Int) {
        val calendar = Calendar.getInstance()
                .apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, day)
                }
        currentNoteLiveData.value = currentNoteLiveData.value?.apply { date = calendar.timeInMillis }
    }

    fun addPhoto(path: String) {
        currentNoteLiveData.value = currentNoteLiveData.value?.apply {
            val updatedPhotos = photos.toMutableList()
            updatedPhotos.add(Photo(UUID.randomUUID().toString(), path, System.currentTimeMillis()))
            photos = updatedPhotos
        }
    }

    fun clearDate() {
        currentNoteLiveData.value = currentNoteLiveData.value?.apply { date = null }
    }

    fun saveNote() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val note = currentNoteLiveData.value
                val title = if (note?.title.isNullOrEmpty())
                    (note?.text ?: "").formattedHint()
                else
                    note?.title

                val id = note?.id
                if (id == null) {
                    notesRepository.createNote(title, note?.text, note?.date,
                            note?.photos ?: arrayListOf())
                } else {
                    notesRepository.editNote(id, title, note.text, note.date)
                }
                withContext(Dispatchers.Main) {
                    saveResultLiveData.value = true
                    currentNoteLiveData.value = NoteData()
                }
            } catch (e: Exception) {
                postError(e)
            }
        }
    }

    fun deleteEditedNote() {
        viewModelScope.launch(Dispatchers.IO) {
            val note = currentNoteLiveData.value
            val title = if (note?.title.isNullOrEmpty())
                (note?.text ?: "").formattedHint()
            else
                note?.title
            notesRepository.deleteDraftedNote(title, note?.text, note?.date)

            withContext(Dispatchers.Main) {
                currentNoteLiveData.value = NoteData()
                deleteResultLiveData.value = true
            }
        }
    }
}