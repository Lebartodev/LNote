package com.lebartodev.lnote.feature_attach

import java.io.File

interface FilesRepository {
    suspend fun loadPhotos(): List<Photo>
    suspend fun copyPhotoToAppDirectory(sourceFile: File): File
}