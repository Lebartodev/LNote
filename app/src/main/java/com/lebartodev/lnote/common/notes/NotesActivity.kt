package com.lebartodev.lnote.common.notes

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.lebartodev.lnote.R
import com.lebartodev.lnote.base.BaseActivity
import com.lebartodev.lnote.data.entity.Note
import com.lebartodev.lnote.di.component.AppComponent
import com.lebartodev.lnote.utils.*
import javax.inject.Inject


class NotesActivity : BaseActivity(), NotesScreen.View {
    private val fabAdd by lazy { findViewById<FloatingActionButton>(R.id.fab_add) }
    private val bottomAppBar by lazy { findViewById<BottomAppBar>(R.id.bottom_app_bar) }
    private val saveNoteButton by lazy { findViewById<MaterialButton>(R.id.save_button) }
    private val titleText by lazy { findViewById<EditText>(R.id.text_title) }
    private val descriptionText by lazy { findViewById<EditText>(R.id.text_description) }
    private val notesList by lazy { findViewById<RecyclerView>(R.id.notes_list) }
    private val adapter = NotesAdapter()
    private val bottomAddSheetBehavior by lazy {
        BottomSheetBehavior.from(findViewById<ConstraintLayout>(R.id.bottom_sheet_add))
    }
    @Inject
    lateinit var viewModelFactory: LNoteViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val vm = ViewModelProviders.of(this, viewModelFactory)[NotesViewModel::class.java]
        vm.loadNotes().observe(this, Observer {
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
        bottomAddSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomAddSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val scaleValue = if (-slideOffset in 0.0..1.0) -slideOffset else if (-slideOffset < 0) 0f else if (-slideOffset > 1) 1f else 0f
                fabAdd.animate().scaleX(scaleValue).scaleY(scaleValue).setDuration(0).start()
                bottomAppBar.animate().translationY((1f - scaleValue) * bottomAppBar.height).setDuration(0).start()
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    hideKeyboard()
                }
            }

        })
        fabAdd.setOnClickListener {
            bottomAddSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        saveNoteButton.setOnClickListener {
            bottomAddSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            vm.saveNote(title = titleText.text.toString(), text = descriptionText.text.toString())
                    .observe(this, Observer { obj ->
                        run {
                            if (obj.error == null) {
                                error("saveNote", obj.error)
                            }
                        }
                    })
            titleText.setText("")
            descriptionText.setText("")

        }
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
        adapter.data = notes
    }

    override fun onLoadError(throwable: Throwable) {
        toast(throwable.message)
    }

    public override fun setupComponent(component: AppComponent) {
        component.inject(this)
    }


}
