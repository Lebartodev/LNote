package com.lebartodev.lnote.show

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lebartodev.core.data.repository.Repository
import javax.inject.Inject

class ShowNoteViewModelFactory @Inject constructor(private val repository: Repository.Notes) :
        ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when (modelClass) {
            ShowNoteViewModel::class.java -> ShowNoteViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}