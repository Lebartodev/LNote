package com.lebartodev.lnote.common.notes

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.lebartodev.lnote.R
import com.lebartodev.lnote.base.BaseActivity
import com.lebartodev.lnote.di.ViewModelFactory
import com.lebartodev.lnote.di.component.AppComponent
import com.lebartodev.lnote.utils.toast
import javax.inject.Inject

class NotesActivity : BaseActivity(), NotesScreen.View {
    private lateinit var fabAdd: FloatingActionButton
    @Inject
    protected lateinit var viewModelFactory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fabAdd = findViewById(R.id.fab_add)
        val vm = ViewModelProviders.of(this, viewModelFactory)[NotesViewModel::class.java]
        vm.notes.observe(this, Observer(::onNotesLoaded))
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
