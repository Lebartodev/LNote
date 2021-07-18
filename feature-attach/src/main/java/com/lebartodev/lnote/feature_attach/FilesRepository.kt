package com.lebartodev.lnote.feature_attach

import com.lebartodev.lnote.feature_attach.di.AttachScope
import java.io.File

@AttachScope
interface FilesRepository {
    suspend fun loadPhotos(): List<Photo>
    suspend fun copyPhotoToAppDirectory(sourceFile: File): File
}