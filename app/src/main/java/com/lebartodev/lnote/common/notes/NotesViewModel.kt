package com.lebartodev.lnote.common.notes

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lebartodev.lnote.repository.NotesRepository
import javax.inject.Inject


class NotesViewModel @Inject constructor(var notesRepository: NotesRepository) : ViewModel(), NotesScreen.ViewModel {

    var notes: LiveData<List<String>> = MutableLiveData()

    override fun loadNotes() {
        notes = LiveDataReactiveStreams.fromPublisher(notesRepository.getNotes().toFlowable())
    }

}