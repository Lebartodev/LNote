package com.lebartodev.lnote.utils.mocks

import com.lebartodev.lnote.common.LNoteApplication
import com.lebartodev.core.di.app.CoreComponent
import com.lebartodev.lnote.utils.di.app.DaggerAppComponentTest

class LNoteApplicationMock : LNoteApplication() {
    override fun createAppComponent(): CoreComponent = DaggerAppComponentTest.builder()
            .applicationContext(this)
            .build()
}