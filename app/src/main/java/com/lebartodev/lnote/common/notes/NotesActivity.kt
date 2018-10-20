package com.lebartodev.lnote.common.notes

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.lebartodev.lnote.R
import com.lebartodev.lnote.base.BaseActivity
import com.lebartodev.lnote.di.component.AppComponent
import com.lebartodev.lnote.utils.toast

class NotesActivity : BaseActivity(), NotesScreen.View {
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var bottomAppBar: BottomAppBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomAppBar = findViewById(R.id.bottom_app_bar)
        fabAdd = findViewById(R.id.fab_add)
        setSupportActionBar(bottomAppBar)
        fabAdd.setOnClickListener {
            toast("Hey")
        }
        val vm = ViewModelProviders.of(this)[NotesViewModel::class.java]
        vm.notes.observe(this, Observer(::onNotesLoaded))
        vm.loadNotes()
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

    override fun setupComponent(component: AppComponent) {
        component.inject(this)
    }


}
