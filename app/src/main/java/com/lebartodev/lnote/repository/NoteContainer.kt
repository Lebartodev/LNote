package com.lebartodev.lnote.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lebartodev.lnote.utils.SingleLiveEvent

object NoteContainer {
    private val currentNoteLiveData = MutableLiveData<Note>().apply { value = Note() }
    private val stateLiveData = SingleLiveEvent<State?>()

    private var tempNote: Note = Note()

    fun currentNote(): LiveData<Note> = currentNoteLiveData

    fun state(): LiveData<State?> = stateLiveData

    data class Note(var title: String? = null,
                    var date: Long? = null,
                    var text: String? = null)

    fun clearCurrentNote() {
        tempNote = currentNoteLiveData.value?.copy() ?: Note()
        currentNoteLiveData.value = Note()
        stateLiveData.value = State.IN_DELETE
    }

    fun undoClearCurrentNote() {
        currentNoteLiveData.value = tempNote.copy()
        tempNote = Note()
    }

    fun deleteTempNote() {
        tempNote = Note()
    }

    fun saveNote() {
        stateLiveData.value = State.IN_SAVE
    }

    enum class State {
        IN_SAVE, IN_DELETE
    }
}