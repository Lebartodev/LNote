package com.lebartodev.lnotes.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lebartodev.core.base.BaseViewModel
import com.lebartodev.core.data.NoteData
import com.lebartodev.core.data.repository.Repository
import com.lebartodev.core.db.entity.Note
import com.lebartodev.lnote.utils.SingleLiveEvent
import com.lebartodev.lnote.utils.exception.DeleteNoteException
import com.lebartodev.lnote.utils.exception.RestoreNoteException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NotesViewModel @Inject constructor(
    private val settingsRepository: Repository.Settings,
    private val notesRepository: Repository.Notes
) : BaseViewModel() {

    private val notesLiveData: MutableLiveData<List<Note>> = MutableLiveData()
    private val bottomPanelEnabledLiveData = MutableLiveData<Boolean?>()
    private val deletedNoteEvent = SingleLiveEvent<Boolean>()
    private val restoredNoteEvent = SingleLiveEvent<NoteData>()

    fun getNotes(): LiveData<List<Note>> = notesLiveData
    fun getRestoredNoteEvent(): SingleLiveEvent<NoteData> = restoredNoteEvent
    fun getDeletedNoteEvent(): SingleLiveEvent<Boolean> = deletedNoteEvent
    fun bottomPanelEnabled(): LiveData<Boolean?> = bottomPanelEnabledLiveData

    init {
        settingsRepository.bottomPanelEnabled()
            .flowOn(Dispatchers.IO)
            .onEach { bottomPanelEnabledLiveData.value = it }
            .catch { postError(it) }
            .launchIn(viewModelScope)
        fetchNotes()
    }

    fun fetchNotes() {
        notesRepository.getNotes()
            .flowOn(Dispatchers.IO)
            .onEach { notesLiveData.value = it }
            .catch { postError(it) }
            .launchIn(viewModelScope)
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch {
            try {
                notesRepository.deleteNote(id)
                withContext(Dispatchers.Main) {
                    deletedNoteEvent.value = true
                }
            } catch (e: Exception) {
                postError(DeleteNoteException(e))
            }
        }
    }

    fun restoreLastNote() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val note = notesRepository.restoreLastNote()
                if (note.created == null) {
                    withContext(Dispatchers.Main) {
                        restoredNoteEvent.value = NoteData(
                            note.id, note.title, note.date,
                            note.text,
                            note.created
                        )
                    }
                }
            } catch (e: Exception) {
                postError(RestoreNoteException(e))
            }
        }
    }
}