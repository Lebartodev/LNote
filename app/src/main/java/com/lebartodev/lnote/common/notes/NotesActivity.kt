package com.lebartodev.lnote.common.notes

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.lebartodev.lnote.R
import com.lebartodev.lnote.base.BaseActivity
import com.lebartodev.lnote.data.entity.Note
import com.lebartodev.lnote.di.component.AppComponent
import com.lebartodev.lnote.utils.LNoteViewModelFactory
import com.lebartodev.lnote.utils.toast
import javax.inject.Inject


class NotesActivity : BaseActivity(), NotesScreen.View {
    private val fabAdd by lazy { findViewById<FloatingActionButton>(R.id.fab_add) }
    private val bottomAppBar by lazy { findViewById<BottomAppBar>(R.id.bottom_app_bar) }
    private val bottomAddSheetBehavior by lazy {
        BottomSheetBehavior.from(findViewById<ConstraintLayout>(R.id.bottom_sheet_add))
    }
    @Inject
    lateinit var viewModelFactory: LNoteViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(bottomAppBar)
        bottomAddSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        fabAdd.setOnClickListener {
            bottomAddSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        val vm = ViewModelProviders.of(this, viewModelFactory)[NotesViewModel::class.java]
        vm.loadNotes().observe(this, Observer(::onNotesLoaded))
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

    override fun onNotesLoaded(notes: List<Note>) {
        for (note in notes) {
            toast(note.title)
        }
    }

    override fun onLoadError(throwable: Throwable) {
        toast(throwable.message)
    }

    override fun setupComponent(component: AppComponent) {
        component.inject(this)
    }


}
