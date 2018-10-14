package com.lebartodev.lnote.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lebartodev.lnote.application.LNoteApplication
import com.lebartodev.lnote.application.component.AppComponent


abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupComponent(LNoteApplication[this].component() as AppComponent)
    }

    protected abstract fun setupComponent(appComponent: AppComponent)
}