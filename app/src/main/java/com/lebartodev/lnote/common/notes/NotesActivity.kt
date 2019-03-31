package com.lebartodev.lnote.common.notes

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.lebartodev.lnote.NoteDialog
import com.lebartodev.lnote.R
import com.lebartodev.lnote.base.BaseActivity
import com.lebartodev.lnote.common.creation.NoteCreationView
import com.lebartodev.lnote.data.entity.Note
import com.lebartodev.lnote.di.component.AppComponent
import com.lebartodev.lnote.di.module.NotesModule
import com.lebartodev.lnote.utils.*
import javax.inject.Inject


class NotesActivity : BaseActivity(), NotesScreen.View, NoteCreationView.SaveClickListener {

    private val fabAdd by lazy { findViewById<FloatingActionButton>(R.id.fab_add) }
    private val bottomAppBar by lazy { findViewById<BottomAppBar>(R.id.bottom_app_bar) }
    private val notesList by lazy { findViewById<RecyclerView>(R.id.notes_list) }
    private val noteCreationView by lazy { findViewById<NoteCreationView>(R.id.bottom_sheet_add) }
    private val adapter = NotesAdapter(object : NotesAdapter.OpenNoteListener {
        override fun onNoteClick(noteId: Long?, title: String?, description: String) {
            val dialog = NoteDialog.startMe(noteId, title, description)
            dialog.show(supportFragmentManager, "NoteDialog")
        }
    })
    private val bottomAddSheetBehavior by lazy {
        BottomSheetBehavior.from(findViewById<ConstraintLayout>(R.id.bottom_sheet_add))
    }
    @Inject
    lateinit var viewModelFactory: LNoteViewModelFactory
    private lateinit var notesViewModel: NotesViewModel

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        noteCreationView.setupActivity(this, viewModelFactory, this)

        notesViewModel = ViewModelProviders.of(this, viewModelFactory)[NotesViewModel::class.java]
        notesViewModel.getNotes().observe(this, Observer {
            if (it.error == null && it.data != null) {
                onNotesLoaded(it.data)
            } else {
                error("loadNotes", it.error)
            }
        })
        setSupportActionBar(bottomAppBar)
        notesList.layoutManager = LinearLayoutManager(this)
        notesList.adapter = adapter
        notesList.addItemDecoration(NotesItemDecoration(8f.toPx(resources),
                8f.toPx(resources),
                16f.toPx(resources),
                16f.toPx(resources)))
        setupBottomSheet()

        fabAdd.setOnClickListener {
            bottomAddSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        notesList.setOnTouchListener { _: View, motionEvent: MotionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN)
                bottomAddSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            false

        }
        notesViewModel.fetchNotes()
    }

    private fun setupBottomSheet() {
        bottomAddSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomAddSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                bottomAppBar.animate().translationY(slideOffset * bottomAppBar.height).setDuration(0).start()
                if (1f - slideOffset == 1f) {
                    fabAdd.show()
                } else {
                    fabAdd.hide()
                }
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN || newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    hideKeyboard(bottomSheet)
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.settings_menu, menu)
        return true
    }

    override fun onSaveClicked() {
        bottomAddSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.app_bar_settings -> {

            }
        }
        return true
    }

    override fun onNotesLoaded(notes: List<Note>) {
        adapter.data = notes
    }

    override fun onLoadError(throwable: Throwable) {
        toast(throwable.message)
    }

    public override fun setupComponent(component: AppComponent) {
        component.plus(NotesModule())
                .inject(this)
    }
}
