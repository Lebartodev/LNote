package com.lebartodev.core.data.repository

import com.lebartodev.core.db.AppDatabase
import com.lebartodev.core.db.entity.Note
import com.lebartodev.core.utils.SchedulersFacade
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import javax.inject.Inject

class NotesRepository @Inject constructor(private val database: AppDatabase, private val schedulersFacade: SchedulersFacade) : Repository.Notes {

    override fun getNotes(): Flowable<List<Note>> = database.notesDao().getAll()
            .subscribeOn(schedulersFacade.io())

    override fun getNote(id: Long): Flowable<Note> = database.notesDao().getById(id)
            .subscribeOn(schedulersFacade.io())

    override fun deleteNote(id: Long): Completable = database.notesDao().markAsDeleted(id, System.currentTimeMillis())
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

    override fun deleteDraftedNote(title: String?, text: String?, date: Long?): Completable {
        return Completable.defer {
            if (text.isNullOrBlank()) {
                Completable.error(NullPointerException())
            } else {
                Completable.fromCallable { database.notesDao().insert(Note(null, title, date, null, text, System.currentTimeMillis())) }
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

    override fun restoreLastNote(): Maybe<Note> {
        return database.notesDao().lastDeleted()
                .flatMap {
                    database.notesDao().restoreNote(it)
                            .andThen(database.notesDao().getById(it).firstElement())
                }
    }

    override fun getArchive(): Flowable<List<Note>> = database.notesDao().getArchivedNotes()
            .subscribeOn(schedulersFacade.io())

}