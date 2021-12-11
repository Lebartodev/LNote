package com.lebartodev.lnote.feature_attach.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lebartodev.core.base.BaseViewModel
import com.lebartodev.lnote.feature_attach.FilesRepository
import com.lebartodev.lnote.feature_attach.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class AttachViewModel @Inject constructor(val filesRepository: FilesRepository) :
    BaseViewModel() {
    val photos = MutableLiveData(listOf<Photo>())
    val selectedPhoto = MutableLiveData<String>()

    init {
        loadPhotos()
    }

    fun loadPhotos() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = filesRepository.loadPhotos()
            withContext(Dispatchers.Main) {
                photos.value = result
            }
        }
    }

    fun attachPhoto(photo: Photo) {
        viewModelScope.launch(Dispatchers.IO)
        {
            val file = filesRepository.copyPhotoToAppDirectory(File(photo.path))
            withContext(Dispatchers.Main) {
                selectedPhoto.value = file.path
            }
        }
    }
}