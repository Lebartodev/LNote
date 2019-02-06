package com.lebartodev.lnote.utils.mocks

import com.lebartodev.lnote.common.LNoteApplication
import com.lebartodev.lnote.utils.di.component.DaggerAppComponentTest
import com.lebartodev.lnote.utils.di.module.AppModuleTest

class LNoteApplicationMock : LNoteApplication() {
    override fun setupGraph() {
        component = DaggerAppComponentTest.builder()
                .appModule(AppModuleTest(this))
                .build()
        component.inject(this)
    }

}