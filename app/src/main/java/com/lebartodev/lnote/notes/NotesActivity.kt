package com.lebartodev.lnote.notes

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.lebartodev.lnote.R
import com.lebartodev.lnote.application.component.AppComponent
import com.lebartodev.lnote.application.component.DaggerNotesComponent
import com.lebartodev.lnote.application.module.NotesModule
import com.lebartodev.lnote.base.BaseActivity
import com.lebartodev.lnote.utils.toast
import javax.inject.Inject

class NotesActivity : BaseActivity(), Notes.View {
    private lateinit var fabAdd: FloatingActionButton
    @Inject
    lateinit var presenter: NotesPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fabAdd = findViewById(R.id.fab_add)
        presenter.loadNotes()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.settings_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.app_bar_settings -> {

            }
        }

        return true
    }

    override fun onNotesLoaded(notes: List<String>) {
        for (note in notes) {
            toast(note)
        }
    }

    override fun onLoadError(throwable: Throwable) {
        toast(throwable.message)
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }

    override fun setupComponent(appComponent: AppComponent) {
        DaggerNotesComponent.builder()
                .notesModule(NotesModule(this))
                .build()
                .inject(this)
    }


}
