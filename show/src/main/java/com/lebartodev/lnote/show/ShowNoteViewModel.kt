package com.lebartodev.lnote.show

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lebartodev.core.base.BaseViewModel
import com.lebartodev.core.data.repository.Repository
import com.lebartodev.core.db.entity.Note
import com.lebartodev.lnote.utils.SingleLiveEvent
import com.lebartodev.lnote.utils.exception.DeleteNoteException
import com.lebartodev.lnote.utils.exception.LoadNoteException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ShowNoteViewModel constructor(private val notesRepository: Repository.Notes) :
        BaseViewModel() {
    private val currentNote = MutableLiveData<Note>()
    private val deleteResultLiveData = SingleLiveEvent<Boolean>()

    fun loadNote(id: Long) {
        notesRepository.getNote(id)
                .flowOn(Dispatchers.IO)
                .onEach { currentNote.value = it }
                .catch { postError(LoadNoteException(it)) }
                .launchIn(viewModelScope)
    }

    fun delete() {
        viewModelScope.launch(Dispatchers.IO) {
            val id = currentNote.value?.id
            try {
                if (id != null) {
                    notesRepository.deleteNote(id)
                    deleteResultLiveData.value = true
                }
            } catch (e: Exception) {
                postError(DeleteNoteException(e))
            }
        }
    }

    fun note(): LiveData<Note> {
        return currentNote
    }

    fun deleteResult(): LiveData<Boolean> {
        return deleteResultLiveData
    }
}
