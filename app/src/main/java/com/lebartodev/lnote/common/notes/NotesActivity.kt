package com.lebartodev.lnote.common.notes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lebartodev.lnote.R


class NotesActivity : AppCompatActivity() {

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
}
