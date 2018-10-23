package com.lebartodev.lnote.common.notes

import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.ViewModel
import com.lebartodev.lnote.repository.NotesRepository


class NotesViewModel constructor(var notesRepository: NotesRepository) : ViewModel(), NotesScreen.ViewModel {
    override fun loadNotes() = LiveDataReactiveStreams.fromPublisher(notesRepository.getNotes())
}