package com.lebartodev.lnote.feature_attach

import com.lebartodev.core.di.utils.FeatureScope
import java.io.File

@FeatureScope
interface FilesRepository {
    suspend fun loadPhotos(): List<Photo>
    suspend fun copyPhotoToAppDirectory(sourceFile: File): File
}