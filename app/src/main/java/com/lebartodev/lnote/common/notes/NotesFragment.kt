package com.lebartodev.lnote.common.notes


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
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
import com.lebartodev.lnote.base.BaseFragment
import com.lebartodev.lnote.common.creation.NoteCreationView
import com.lebartodev.lnote.data.entity.Note
import com.lebartodev.lnote.di.component.AppComponent
import com.lebartodev.lnote.di.module.NotesModule
import com.lebartodev.lnote.utils.*
import javax.inject.Inject


class NotesFragment : BaseFragment(), NoteCreationView.SaveClickListener, NoteCreationView.FullScreenListener {
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var bottomAppBar: BottomAppBar
    private lateinit var notesList: RecyclerView
    private lateinit var noteCreationView: NoteCreationView
    private val adapter = NotesAdapter(object : NotesAdapter.OpenNoteListener {
        override fun onNoteClick(noteId: Long?, title: String?, description: String) {
            val dialog = NoteDialog.startMe(noteId, title, description)
            dialog.show(fragmentManager, "NoteDialog")
        }
    })
    private lateinit var bottomAddSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    @Inject
    lateinit var viewModelFactory: LNoteViewModelFactory
    private lateinit var notesViewModel: NotesViewModel

    private var isBottomSheetOpen: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notes, container, false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fabAdd = view.findViewById(R.id.fab_add)
        bottomAppBar = view.findViewById(R.id.bottom_app_bar)
        notesList = view.findViewById(R.id.notes_list)
        noteCreationView = view.findViewById(R.id.bottom_sheet_add)
        bottomAddSheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.bottom_sheet_add))
        noteCreationView.setupFragment(this, viewModelFactory, this, this)
        notesViewModel = ViewModelProviders.of(this, viewModelFactory)[NotesViewModel::class.java]
        notesViewModel.getNotes().observe(this, Observer {
            if (it.error == null && it.data != null) {
                onNotesLoaded(it.data)
            } else {
                error("loadNotes", it.error)
            }
        })
        notesList.layoutManager = LinearLayoutManager(context)
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
                    // hideKeyboard(bottomSheet)
                    isBottomSheetOpen = false
                }
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    // hideKeyboard(bottomSheet)
                    isBottomSheetOpen = true
                }

            }
        })
        if (isBottomSheetOpen) {
            bottomAddSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.settings_menu, menu)
//        return true
//    }

    override fun onSaveClicked() {
        bottomAddSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun onFullScreenClicked() {
        // bottomAddSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.app_bar_settings -> {

            }
        }
        return true
    }


    fun onNotesLoaded(notes: List<Note>) {
        adapter.data = notes
    }

    fun onLoadError(throwable: Throwable) {
        toast(throwable.message)
    }

    public override fun setupComponent(component: AppComponent) {
        component.plus(NotesModule())
                .inject(this)
    }

}
