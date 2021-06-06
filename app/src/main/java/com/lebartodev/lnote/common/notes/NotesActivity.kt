package com.lebartodev.lnote.common.notes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import com.lebartodev.lnote.R
import com.lebartodev.lnote.common.EditorEvent
import com.lebartodev.lnote.common.EditorEventCallback
import com.lebartodev.lnote.common.EditorEventContainer


class NotesActivity : AppCompatActivity(), EditorEventContainer {
    private var currentEditorEvent: EditorEvent? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fragment = supportFragmentManager.findFragmentByTag(NotesFragment.TAG)
        if (fragment == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.notes_layout_container, NotesFragment(), NotesFragment.TAG)
                    .commit()
        }
    }

    override fun popEditorEvent(): EditorEvent? {
        val res = currentEditorEvent
        currentEditorEvent = null
        return res
    }

    override fun deleteNote() {
        currentEditorEvent = EditorEvent.DELETE
    }

    override fun saveNote() {
        currentEditorEvent = EditorEvent.SAVE
    }

}
