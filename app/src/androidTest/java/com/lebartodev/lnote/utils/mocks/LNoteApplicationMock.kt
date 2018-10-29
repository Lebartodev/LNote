package com.lebartodev.lnote.utils.mocks

import com.lebartodev.lnote.common.LNoteApplication
import com.lebartodev.lnote.utils.di.component.DaggerAppComponentTest

class LNoteApplicationMock : LNoteApplication() {
    override fun setupGraph() {
        component = DaggerAppComponentTest.builder()
                .withApplication(this)
                .build()
    }

}