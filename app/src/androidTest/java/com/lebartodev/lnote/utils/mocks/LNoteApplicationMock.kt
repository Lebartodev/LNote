package com.lebartodev.lnote.utils.mocks

import com.lebartodev.lnote.common.LNoteApplication
import com.lebartodev.lnote.utils.di.app.AppModuleTest
import com.lebartodev.lnote.utils.di.app.DaggerAppComponentTest

class LNoteApplicationMock : LNoteApplication() {
    override fun setupGraph() {
        component = DaggerAppComponentTest.builder()
                .appModule(AppModuleTest(this))
                .build()
        component.inject(this)
    }

}