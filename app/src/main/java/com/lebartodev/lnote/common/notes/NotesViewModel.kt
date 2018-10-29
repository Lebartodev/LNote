package com.lebartodev.lnote.common.notes

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.ViewModel
import com.lebartodev.lnote.data.entity.Note
import com.lebartodev.lnote.data.entity.ViewModelObject
import com.lebartodev.lnote.repository.NotesRepository
import com.lebartodev.lnote.utils.DebugOpenClass

@DebugOpenClass
class NotesViewModel constructor(var notesRepository: NotesRepository) : ViewModel(), NotesScreen.ViewModel {
    fun saveNote(title: String?, text: String): LiveData<ViewModelObject<Long>> =
            LiveDataReactiveStreams.fromPublisher(notesRepository.createNote(title, text).toFlowable()
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