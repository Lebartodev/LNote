package com.lebartodev.lnote.utils.mocks

import com.lebartodev.lnote.common.LNoteApplication
import com.lebartodev.core.di.app.AppComponent
import com.lebartodev.lnote.utils.di.app.DaggerAppComponentTest

class LNoteApplicationMock : LNoteApplication() {
    override fun createAppComponent(): AppComponent = DaggerAppComponentTest.builder()
            .applicationContext(this)
            .build()
}