package com.lebartodev.lnote.repository

import com.lebartodev.lnote.data.AppDatabase
import com.lebartodev.lnote.data.entity.Note
import com.lebartodev.lnote.utils.SchedulersFacade
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

class NotesRepository @Inject constructor(private val database: AppDatabase, private val schedulersFacade: SchedulersFacade) : Repository.Notes {

    override fun getNotes(): Flowable<List<Note>> = database.notesDao().getAll()
            .subscribeOn(schedulersFacade.io())

    override fun getNote(id: Long): Flowable<Note> = database.notesDao().getById(id)
            .subscribeOn(schedulersFacade.io())

    override fun deleteNote(id: Long): Completable = Completable.fromCallable { database.notesDao().deleteById(id) }
            .subscribeOn(schedulersFacade.io())

    override fun createNote(title: String?, text: String?, date: Long?): Single<Long> {
        return Single.defer {
            if (text.isNullOrBlank()) {
                Single.error(NullPointerException())
            } else {
                Single.fromCallable { database.notesDao().insert(Note(null, title, date, System.currentTimeMillis(), text)) }
                        .subscribeOn(schedulersFacade.io())
            }

        }
    }

    override fun editNote(id: Long, title: String?, text: String?, date: Long?): Completable {
        return Completable.defer {
            if (text.isNullOrBlank()) {
                Completable.error(NullPointerException())
            } else {
                database.notesDao().getById(id)
                        .firstOrError()
                        .map {
                            it.text = text
                            it.date = date
                            it.title = title
                            it
                        }
                        .flatMapCompletable { Completable.fromAction { database.notesDao().update(it) } }
                        .subscribeOn(schedulersFacade.io())

            }
        }
    }

    override fun restoreNote(id: Long?, title: String?, text: String?, date: Long?, createdDate: Long?): Single<Long> {
        return Single.defer {
            if (text.isNullOrBlank()) {
                Single.error(NullPointerException())
            } else {
                Single.fromCallable { database.notesDao().insert(Note(id, title, date, createdDate, text)) }
                        .subscribeOn(schedulersFacade.io())
            }

        }
    }


}