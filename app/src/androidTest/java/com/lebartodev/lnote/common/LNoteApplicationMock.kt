package com.lebartodev.lnote.common

import com.lebartodev.lnote.di.component.DaggerAppComponentMock

class LNoteApplicationMock : LNoteApplication() {
    override fun setupGraph() {
        component = DaggerAppComponentMock.builder()
                .withApplication(this)
                .build()
    }

}