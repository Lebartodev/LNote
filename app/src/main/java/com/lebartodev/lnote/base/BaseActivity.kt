package com.lebartodev.lnote.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lebartodev.lnote.common.LNoteApplication
import com.lebartodev.lnote.di.component.AppComponent


abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupComponent(LNoteApplication[this].component())
    }

    protected abstract fun setupComponent(component: AppComponent)
}