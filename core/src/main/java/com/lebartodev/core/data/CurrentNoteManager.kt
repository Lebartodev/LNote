package com.lebartodev.core.data

import com.lebartodev.core.di.utils.AppScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@AppScope
class CurrentNoteManager @Inject constructor() : Manager.CurrentNote {
    private val currentNote: MutableStateFlow<NoteData> = MutableStateFlow(NoteData())

    override fun currentNote(): Flow<NoteData> = currentNote

    override fun clear() {
        currentNote.value = NoteData()
    }

    override fun setState(reducer: NoteData.() -> NoteData) {
        val noteData = currentNote.value.reducer()
        currentNote.value = noteData
    }
}