package com.lebartodev.lnote.application.module

import android.app.Application
import com.lebartodev.lnote.application.LNoteApplication
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class AppModule(private val lNoteApplication: LNoteApplication) {

    @Provides
    @Singleton
    fun provideApplication(): Application {
        return lNoteApplication
    }
}