package com.lebartodev.lnote.repository

import com.lebartodev.lnote.data.AppDatabase
import com.lebartodev.lnote.data.entity.Note
import com.lebartodev.lnote.utils.SchedulersFacade
import io.reactivex.Single
import javax.inject.Inject


class NotesRepository @Inject constructor(var database: AppDatabase,
                                          var schedulersFacade: SchedulersFacade) {

    fun getNotes(): Single<List<Note>> {
        return Single.just(database.notesDao().getAll()).subscribeOn(schedulersFacade.io())
    }

    fun createNote(): Single<List<String>> {
        return Single.just(arrayListOf("a", "b", "c"))
    }
}