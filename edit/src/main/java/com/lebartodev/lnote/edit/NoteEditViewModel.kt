package com.lebartodev.lnote.edit

import android.util.Log
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.lang.NullPointerException
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject

@Suppress("Detekt.TooManyFunctions")
@FeatureScope
class NoteEditViewModel @Inject constructor(
    private val notesRepository: Repository.Notes,
    private val currentNoteRepository: Manager.CurrentNote
) : BaseViewModel() {
    private val saveResultLiveData: SingleLiveEvent<Boolean> = SingleLiveEvent()
    private val deleteResultLiveData: SingleLiveEvent<Boolean> = SingleLiveEvent()
    private val currentNoteLiveData = MutableLiveData(NoteData())

    init {
        currentNoteRepository.currentNote()
            .flowOn(Dispatchers.IO)
            .onEach { currentNoteLiveData.postValue(it) }
            .launchIn(viewModelScope)
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
            Log.d("Lebartodev", "loadNote: ${note.photos}")
            currentNoteRepository.setState {
                copy(
                    id = note.id,
                    title = note.title,
                    date = note.date,
                    text = note.text,
                    dateCreated = note.created,
                    photos = note.photos
                ).also {
                    Log.d("Lebartodev", "loadNote: ${it.photos}")
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
                val photo = Photo(UUID.randomUUID().toString(), path, System.currentTimeMillis())
                updatedPhotos.add(photo)
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
            } catch (exception: Exception) {
                postError(exception)
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