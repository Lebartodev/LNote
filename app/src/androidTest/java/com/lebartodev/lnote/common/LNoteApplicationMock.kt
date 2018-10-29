package com.lebartodev.lnote.common

import com.lebartodev.lnote.utils.di.component.DaggerAppComponentTest

class LNoteApplicationMock : LNoteApplication() {
    override fun setupGraph() {
        component = DaggerAppComponentTest.builder()
                .withApplication(this)
                .build()
    }

}