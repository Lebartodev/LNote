package com.lebartodev.lnote.common.notes


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Fade
import androidx.transition.Slide
import androidx.transition.TransitionInflater
import androidx.transition.TransitionSet
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.lebartodev.lnote.R
import com.lebartodev.lnote.base.BaseFragment
import com.lebartodev.lnote.common.details.EditNoteFragment
import com.lebartodev.lnote.common.details.NoteCreationView
import com.lebartodev.lnote.common.details.NoteEditViewModel
import com.lebartodev.lnote.data.entity.Note
import com.lebartodev.lnote.data.entity.Status
import com.lebartodev.lnote.di.app.AppComponent
import com.lebartodev.lnote.di.notes.NotesModule
import com.lebartodev.lnote.utils.LNoteViewModelFactory
import com.lebartodev.lnote.utils.error
import com.lebartodev.lnote.utils.ui.NotesItemDecoration
import com.lebartodev.lnote.utils.ui.toPx
import javax.inject.Inject


class NotesFragment : BaseFragment() {
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var bottomAppBar: BottomAppBar
    private lateinit var notesList: RecyclerView
    private lateinit var noteCreationView: NoteCreationView
    private lateinit var adapter: NotesAdapter
    private lateinit var bottomAddSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    @Inject
    lateinit var viewModelFactory: LNoteViewModelFactory
    private lateinit var notesViewModel: NotesViewModel
    private lateinit var editNoteViewModel: NoteEditViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notesViewModel = ViewModelProviders.of(this, viewModelFactory)[NotesViewModel::class.java]
        editNoteViewModel = activity?.run { ViewModelProviders.of(this, viewModelFactory)[NoteEditViewModel::class.java] } ?: throw NullPointerException()
        adapter = NotesAdapter {
            val nextFragment = EditNoteFragment.initMe(it.title, null, it.text, it.date)
            val exitFade = Fade(Fade.OUT).apply {
                duration = resources.getInteger(R.integer.animation_duration).toLong()
            }
            val enterSlide = Slide(Gravity.END).apply {
                duration = resources.getInteger(R.integer.animation_duration).toLong()
            }
            nextFragment.enterTransition = enterSlide
            this.exitTransition = exitFade

            val transaction = fragmentManager
                    ?.beginTransaction()
                    ?.replace(R.id.notes_layout_container, nextFragment)
                    ?.addToBackStack(null)
            transaction?.commit()
        }

        notesViewModel.noteDeleteDialog().observe(this, Observer {
            if (it == true) {
                bottomAppBar.translationY = bottomAppBar.height * 1f
                view?.run {
                    val snackBar = Snackbar.make(this, R.string.note_deleted, Snackbar.LENGTH_LONG)
                            .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                                override fun onShown(transientBottomBar: Snackbar?) {
                                    super.onShown(transientBottomBar)
                                    fabAdd.hide()
                                    bottomAppBar.hideOnScroll = false
                                    bottomAppBar.visibility = View.INVISIBLE
                                }

                                override fun onDismissed(transientBottomBar: Snackbar?,
                                                         event: Int) {
                                    super.onDismissed(transientBottomBar, event)
                                    bottomAppBar.visibility = View.VISIBLE
                                    bottomAppBar.hideOnScroll = true
                                    bottomAppBar.animate().translationY(0f).setDuration(200).start()
                                    editNoteViewModel.deleteTempNote()
                                    fabAdd.show()
                                }
                            })
                            .setAction(R.string.undo) {
                                notesViewModel.toggleBottomSheet()
                                editNoteViewModel.undoClearCurrentNote()
                            }
                            .setActionTextColor(ContextCompat.getColor(this.context, R.color.colorAction))
                    val layout = snackBar.view as Snackbar.SnackbarLayout
                    val textView = layout.findViewById(
                            com.google.android.material.R.id.snackbar_text) as TextView
                    textView.setTextColor(ContextCompat.getColor(this.context, R.color.white))
                    snackBar.show()
                }
            }
        })

        notesViewModel.getNotes().observe(this, Observer {
            val list = it.data
            if (it.error == null && list != null) {
                onNotesLoaded(list)
            } else {
                error("loadNotes", it.error)
            }
        })

        editNoteViewModel.deleteNoteState().observe(this, Observer {
            if (it == true) {
                notesViewModel.onNoteDeleted()
            }
        })
        editNoteViewModel.saveResult().observe(this, Observer {
            if (it != null)
                if (it.status == Status.SUCCESS) {
                    if (notesViewModel.bottomSheetOpen().value == true) {
                        notesViewModel.toggleBottomSheet()
                    }
                } else if (it.status == Status.ERROR) {
                    context?.run { Toast.makeText(this, getString(R.string.error_note_create), Toast.LENGTH_SHORT).show() }
                }

        })

        notesViewModel.bottomSheetOpen().observe(this, Observer {
            if (it == false) {
                bottomAddSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            } else if (it == true) {
                bottomAddSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        })
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
        notesList.layoutManager = LinearLayoutManager(context)
        notesList.adapter = adapter
        notesList.addItemDecoration(NotesItemDecoration(8f.toPx(resources),
                8f.toPx(resources),
                16f.toPx(resources),
                16f.toPx(resources)))



        setupBottomSheet()
        fabAdd.setOnClickListener { notesViewModel.toggleBottomSheet() }
        notesList.setOnTouchListener { _: View, motionEvent: MotionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN)
                bottomAddSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            false
        }

    }

    private fun setupBottomSheet() {
        bottomAddSheetBehavior.setBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
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
                    if (notesViewModel.bottomSheetOpen().value != false)
                        notesViewModel.toggleBottomSheet()
                }
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    hideKeyboard(bottomSheet)
                    if (notesViewModel.bottomSheetOpen().value != true)
                        notesViewModel.toggleBottomSheet()
                }
            }
        })
        if (notesViewModel.bottomSheetOpen().value == true) {
            bottomAddSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
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
        component.plus(NotesModule()).inject(this)
    }

    companion object {
        const val TAG = "NotesFragment"
    }

    private fun setupEditViewModel() {
        editNoteViewModel.fullScreenOpen().observe(this, Observer {
            if (it == true) {
                hideKeyboardListener(noteCreationView.titleText) {
                    val nextFragment = EditNoteFragment
                            .initMe(noteCreationView.titleText.text.toString(),
                                    noteCreationView.titleText.hint.toString(),
                                    noteCreationView.descriptionText.text.toString(),
                                    editNoteViewModel.selectedDate().value)
                    this.exitTransition = Fade(Fade.OUT)
                            .apply { duration = resources.getInteger(R.integer.animation_duration).toLong() / 2 }

                    this.returnTransition = Fade(Fade.IN)
                            .apply { duration = resources.getInteger(R.integer.animation_duration).toLong() }

                    nextFragment.enterTransition = Fade(Fade.IN)
                            .apply {
                                startDelay = resources.getInteger(R.integer.animation_duration).toLong() / 2
                                duration = resources.getInteger(R.integer.animation_duration).toLong() / 2
                            }


                    nextFragment.sharedElementEnterTransition = TransitionSet()
                            .apply {
                                addTransition(TransitionInflater.from(context).inflateTransition(android.R.transition.move))
                                startDelay = 0
                                duration = resources.getInteger(R.integer.animation_duration).toLong()
                            }

                    val transaction = this.fragmentManager
                            ?.beginTransaction()
                            ?.replace(R.id.notes_layout_container, nextFragment)
                            ?.addSharedElement(noteCreationView.titleText, noteCreationView.titleText.transitionName)
                            ?.addSharedElement(noteCreationView.background, noteCreationView.background.transitionName)
                            ?.addSharedElement(noteCreationView.saveNoteButton, noteCreationView.saveNoteButton.transitionName)
                            ?.addSharedElement(noteCreationView.descriptionText, noteCreationView.descriptionText.transitionName)
                            ?.addSharedElement(noteCreationView.fullScreenButton, noteCreationView.fullScreenButton.transitionName)
                            ?.addSharedElement(noteCreationView.dateChip, noteCreationView.dateChip.transitionName)
                            ?.addSharedElement(noteCreationView.divider, noteCreationView.divider.transitionName)
                    if (noteCreationView.deleteButton.visibility == View.VISIBLE && noteCreationView.calendarButton.visibility == View.VISIBLE) {
                        transaction?.addSharedElement(noteCreationView.deleteButton, noteCreationView.deleteButton.transitionName)
                                ?.addSharedElement(noteCreationView.calendarButton, noteCreationView.calendarButton.transitionName)
                    }
                    transaction?.addToBackStack(null)
                    transaction?.commit()
                }
            }
        })
        editNoteViewModel.currentNote().observe(this, Observer { noteCreationView.updateNoteData(it) })
        editNoteViewModel.isMoreOpen().observe(this, Observer { noteCreationView.updateMoreState(it) })
    }
}
