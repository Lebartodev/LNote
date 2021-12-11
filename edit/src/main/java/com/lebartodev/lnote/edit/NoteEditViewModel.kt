package com.lebartodev.lnote.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lebartodev.core.base.BaseViewModel
import com.lebartodev.core.data.Manager
import com.lebartodev.core.data.NoteData
import com.lebartodev.core.data.isEmpty
import com.lebartodev.core.data.repository.Repository
import com.lebartodev.core.db.entity.Photo
import com.lebartodev.core.di.utils.FeatureScope
import com.lebartodev.lnote.utils.SingleLiveEvent
import com.lebartodev.lnote.utils.extensions.formattedHint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

@FeatureScope
class NoteEditViewModel @Inject constructor(
    private val notesRepository: Repository.Notes,
    private val currentNoteRepository: Manager.CurrentNote
) : BaseViewModel() {
    private val saveResultLiveData: SingleLiveEvent<Boolean> = SingleLiveEvent()
    private val deleteResultLiveData: SingleLiveEvent<Boolean> = SingleLiveEvent()
    private val currentNoteLiveData = MutableLiveData(NoteData())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            currentNoteRepository.currentNote()
                .onEach { currentNoteLiveData.postValue(it) }
                .collect()
        }

    }

    fun currentNote(): LiveData<NoteData> = currentNoteLiveData

    fun saveResult(): LiveData<Boolean?> = saveResultLiveData

    fun deleteResult(): LiveData<Boolean?> = deleteResultLiveData

    fun loadNote(id: Long?) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentNote = currentNoteRepository.currentNote().first()
            if (currentNote.id != null) {
                currentNoteRepository.clear()
            }
            if (id == null) return@launch
            val note = notesRepository.getNote(id).first()
            withContext(Dispatchers.Main) {
                currentNoteRepository.setState {
                    copy(
                        note.id, note.title, note.date,
                        note.text, note.created, note.photos
                    )
                }
            }

        }
    }

    fun setDescription(value: String) {
        currentNoteRepository.setState { copy(text = value) }
    }

    fun setTitle(value: String) {
        currentNoteRepository.setState { copy(title = value) }
    }

    fun setDate(year: Int, month: Int, day: Int) {
        val calendar = Calendar.getInstance()
            .apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, day)
            }
        currentNoteRepository.setState { copy(date = calendar.timeInMillis) }
    }

    fun addPhoto(path: String) {
        currentNoteRepository.setState {
            copy(photos = photos.let {
                val updatedPhotos = it.toMutableList()
                updatedPhotos.add(
                    Photo(UUID.randomUUID().toString(), path, System.currentTimeMillis())
                )
                updatedPhotos
            })
        }
    }

    fun clearDate() {
        currentNoteRepository.setState { copy(date = null) }
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
                    notesRepository.createNote(
                        title, note?.text, note?.date,
                        note?.photos ?: arrayListOf()
                    )
                } else {
                    notesRepository.editNote(id, title, note.text, note.date)
                }
                withContext(Dispatchers.Main) {
                    saveResultLiveData.value = true
                    currentNoteRepository.clear()
                }
            } catch (e: Exception) {
                postError(e)
            }
        }
    }

    fun deleteEditedNote() {
        viewModelScope.launch(Dispatchers.IO) {
            val note = currentNoteLiveData.value ?: return@launch
            if (note.isEmpty()) return@launch
            val title = if (note.title.isNullOrEmpty())
                (note.text ?: "").formattedHint()
            else
                note.title
            notesRepository.deleteDraftedNote(title, note.text, note.date, note.photos)

            withContext(Dispatchers.Main) {
                currentNoteRepository.clear()
                deleteResultLiveData.value = true
            }
        }
    }
}