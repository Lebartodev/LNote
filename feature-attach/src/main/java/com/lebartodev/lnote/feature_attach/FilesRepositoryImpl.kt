package com.lebartodev.lnote.feature_attach

import android.content.Context
import android.provider.MediaStore
import com.lebartodev.core.di.utils.FeatureScope
import java.io.File
import javax.inject.Inject

class FilesRepositoryImpl @Inject constructor(private val applicationContext: Context) :
    FilesRepository {

    private val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.DATA
    )

    override suspend fun loadPhotos(): List<Photo> {
        val result = mutableListOf<Photo>()
        applicationContext.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection, null, null, MediaStore.Images.Media.DATE_ADDED + " DESC"
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            while (cursor.moveToNext()) {
                val path = cursor.getString(pathColumn)
                val name = cursor.getString(nameColumn)
                result += Photo(path, name)
            }
        }
        return result
    }

    override suspend fun copyPhotoToAppDirectory(sourceFile: File): File {
        val targetName = sourceFile.name
        val targetFile = File(applicationContext.filesDir, targetName)
        return if (targetFile.exists()) {
            targetFile
        } else {
            sourceFile.copyTo(File(applicationContext.filesDir, targetName))
        }
    }
}