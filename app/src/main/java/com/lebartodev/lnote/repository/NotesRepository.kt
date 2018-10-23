package com.lebartodev.lnote.repository

import com.lebartodev.lnote.data.AppDatabase
import com.lebartodev.lnote.data.entity.Note
import com.lebartodev.lnote.utils.SchedulersFacade
import io.reactivex.Flowable
import io.reactivex.Single


class NotesRepository constructor(var database: AppDatabase,
                                  var schedulersFacade: SchedulersFacade) {

    fun getNotes(): Flowable<List<Note>> = database.notesDao().getAll().subscribeOn(schedulersFacade.io())


    fun createNote(title: String?, text: String): Single<Long> = Single.just(database.notesDao()
            .insert(Note(null, title, System.currentTimeMillis(), text)))
            .subscribeOn(schedulersFacade.io())
}