package com.lebartodev.lnote.common.notes


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Fade
import androidx.transition.Slide
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.lebartodev.lnote.R
import com.lebartodev.lnote.base.BaseFragment
import com.lebartodev.lnote.common.details.EditNoteFragment
import com.lebartodev.lnote.common.details.NoteCreationView
import com.lebartodev.lnote.data.entity.Note
import com.lebartodev.lnote.di.app.AppComponent
import com.lebartodev.lnote.di.notes.NotesModule
import com.lebartodev.lnote.repository.NoteContainer
import com.lebartodev.lnote.utils.LNoteViewModelFactory
import com.lebartodev.lnote.utils.ui.NotesItemDecoration
import com.lebartodev.lnote.utils.error
import com.lebartodev.lnote.utils.ui.toPx
import javax.inject.Inject


class NotesFragment : BaseFragment(), NoteCreationView.ClickListener {
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var bottomAppBar: BottomAppBar
    private lateinit var notesList: RecyclerView
    private lateinit var noteCreationView: NoteCreationView
    private lateinit var adapter: NotesAdapter
    private lateinit var bottomAddSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    @Inject
    lateinit var viewModelFactory: LNoteViewModelFactory
    private lateinit var notesViewModel: NotesViewModel

    private var isBottomSheetOpen: Boolean = false
    private var isMoreOpen: Boolean = false
    private var snackBarShowing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notesViewModel = ViewModelProviders.of(this, viewModelFactory)[NotesViewModel::class.java]
        notesViewModel.getNotes().observe(this, Observer {
            val list = it.data
            if (it.error == null && list != null) {
                onNotesLoaded(list)
            } else {
                error("loadNotes", it.error)
            }
        })
        adapter = NotesAdapter {
            val nextFragment = EditNoteFragment.startMe(it.title, null, it.text, it.date)
            val exitFade = Fade(Fade.OUT).apply {
                duration = resources.getInteger(R.integer.animation_duration).toLong()
            }
            val enderSlide = Slide(Gravity.END).apply {
                duration = resources.getInteger(R.integer.animation_duration).toLong()
            }
            nextFragment.enterTransition = enderSlide
            this.exitTransition = exitFade

            val transaction = fragmentManager
                    ?.beginTransaction()
                    ?.replace(R.id.notes_layout_container, nextFragment)
                    ?.addToBackStack(null)
            transaction?.commit()
        }
        notesViewModel.fetchNotes()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
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
        noteCreationView.setupFragment(this, viewModelFactory, this, isMoreOpen)
        notesList.layoutManager = LinearLayoutManager(context)
        notesList.adapter = adapter
        notesList.addItemDecoration(NotesItemDecoration(8f.toPx(resources),
                8f.toPx(resources),
                16f.toPx(resources),
                16f.toPx(resources)))
        if (NoteContainer.isSaved) {
            isBottomSheetOpen = false
            bottomAddSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            NoteContainer.isSaved = false
        }

        setupBottomSheet()

        if (NoteContainer.isDeleted) {
            onDeleteClicked()
            NoteContainer.isDeleted = false
        }
        fabAdd.setOnClickListener {
            bottomAddSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        notesList.setOnTouchListener { _: View, motionEvent: MotionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN)
                bottomAddSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            false
        }
    }

    private fun setupBottomSheet() {
        bottomAddSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (!snackBarShowing)
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
                    isBottomSheetOpen = false
                }
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    hideKeyboard(bottomSheet)
                    isBottomSheetOpen = true
                }
            }
        })
        if (isBottomSheetOpen) {
            bottomAddSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    override fun onSaveClicked() {
        bottomAddSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun onFullScreenClicked() {
        isMoreOpen = noteCreationView.isMoreOpen
    }

    override fun onDeleteClicked() {
        snackBarShowing = true
        bottomAddSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomAppBar.translationY = bottomAppBar.height * 1f
        view?.let {
            val snackBar = Snackbar.make(it, R.string.note_deleted, Snackbar.LENGTH_LONG)
                    .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                        override fun onShown(transientBottomBar: Snackbar?) {
                            super.onShown(transientBottomBar)
                            fabAdd.hide()
                            bottomAppBar.hideOnScroll = false
                            bottomAppBar.visibility = View.INVISIBLE
                        }

                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            super.onDismissed(transientBottomBar, event)
                            bottomAppBar.visibility = View.VISIBLE
                            snackBarShowing = false
                            bottomAppBar.hideOnScroll = true
                            bottomAppBar.animate().translationY(0f).setDuration(200).start()
                            NoteContainer.tempNote.text = null
                            NoteContainer.tempNote.title = null
                            NoteContainer.tempNote.date = null
                            fabAdd.show()
                        }
                    })
                    .setAction(R.string.undo) {
                        NoteContainer.currentNote.title = NoteContainer.tempNote.title
                        NoteContainer.currentNote.text = NoteContainer.tempNote.text
                        NoteContainer.currentNote.date = NoteContainer.tempNote.date
                        noteCreationView.setContent(NoteContainer.currentNote.title, NoteContainer.currentNote.text)
                        bottomAddSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                    .setActionTextColor(ContextCompat.getColor(it.context, R.color.colorAction))
            val layout = snackBar.view as Snackbar.SnackbarLayout
            val textView = layout.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
            textView.setTextColor(ContextCompat.getColor(it.context, R.color.white))
            snackBar.show()
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.app_bar_settings -> {

            }
        }
        return true
    }


    private fun onNotesLoaded(notes: List<Note>) {
        adapter.data = notes
    }

    public override fun setupComponent(component: AppComponent) {
        component.plus(NotesModule())
                .inject(this)
    }

}
