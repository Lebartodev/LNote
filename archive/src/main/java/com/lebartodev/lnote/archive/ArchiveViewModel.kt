package com.lebartodev.lnote.archive

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lebartodev.core.base.BaseViewModel
import com.lebartodev.core.data.repository.Repository
import com.lebartodev.core.db.entity.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ArchiveViewModel constructor(var notesRepository: Repository.Notes) : BaseViewModel() {

    private val notesLiveData: MutableLiveData<List<Note>> = MutableLiveData()

    fun getNotes(): LiveData<List<Note>> = notesLiveData

    init {
        notesRepository.getArchive()
                .flowOn(Dispatchers.IO)
                .onEach { notesLiveData.value = it }
                .catch { postError(it) }
                .launchIn(viewModelScope)
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            notesRepository.completleDeleteNote(id)
        }
    }

    fun restoreNote(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            notesRepository.restoreNote(id)
        }
    }
}