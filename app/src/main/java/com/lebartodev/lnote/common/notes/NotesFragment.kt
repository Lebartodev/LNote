package com.lebartodev.lnote.common.notes

import android.animation.Animator
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
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
import com.lebartodev.lnote.common.details.ShowNoteFragment
import com.lebartodev.lnote.common.edit.EditNoteFragment
import com.lebartodev.lnote.common.edit.NoteCreationView
import com.lebartodev.lnote.common.edit.NoteEditViewModel
import com.lebartodev.lnote.common.settings.SettingsBottomView
import com.lebartodev.lnote.data.entity.Note
import com.lebartodev.lnote.data.entity.Status
import com.lebartodev.lnote.di.app.AppComponent
import com.lebartodev.lnote.di.notes.NotesModule
import com.lebartodev.lnote.utils.LNoteViewModelFactory
import com.lebartodev.lnote.utils.error
import com.lebartodev.lnote.utils.ui.LockableBottomSheetBehavior
import com.lebartodev.lnote.utils.ui.NotesItemDecoration
import com.lebartodev.lnote.utils.ui.toPx
import java.util.*
import javax.inject.Inject

class NotesFragment : BaseFragment() {
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var bottomAppBar: BottomAppBar
    private lateinit var notesList: RecyclerView
    private lateinit var noteCreationView: NoteCreationView
    private lateinit var settingsView: SettingsBottomView
    private val adapter: NotesAdapter = NotesAdapter {
        it.id?.run {
            fun showFragment() {
                val nextFragment = ShowNoteFragment.initMe(this@run)
                val exitFade = Fade(Fade.OUT).apply {
                    duration = resources.getInteger(R.integer.animation_duration).toLong()
                }
                val enterSlide = Slide(Gravity.END).apply {
                    duration = resources.getInteger(R.integer.animation_duration).toLong()
                }
                nextFragment.enterTransition = enterSlide
                exitTransition = exitFade

                val transaction = fragmentManager
                        ?.beginTransaction()
                        ?.replace(R.id.notes_layout_container, nextFragment)
                        ?.addToBackStack(ShowNoteFragment.BACK_STACK_TAG)
                transaction?.commit()
            }

            bottomAppBar.animate().translationY(1f * bottomAppBar.height).setDuration(100)
                    .setListener(object : Animator.AnimatorListener {
                        override fun onAnimationRepeat(animation: Animator?) {

                        }

                        override fun onAnimationEnd(animation: Animator?) {
                            if (fabAdd.isShown) {
                                fabAdd.hide(object : FloatingActionButton.OnVisibilityChangedListener() {
                                    override fun onHidden(fab: FloatingActionButton?) {
                                        super.onHidden(fab)
                                        showFragment()
                                    }
                                })
                            } else {
                                showFragment()
                            }
                        }

                        override fun onAnimationCancel(animation: Animator?) {
                        }

                        override fun onAnimationStart(animation: Animator?) {
                        }

                    })
                    .start()
        }
    }
    private lateinit var bottomAddSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var settingsSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    @Inject
    lateinit var viewModelFactory: LNoteViewModelFactory
    private lateinit var notesViewModel: NotesViewModel
    private lateinit var editNoteViewModel: NoteEditViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notesViewModel = ViewModelProviders.of(this, viewModelFactory)[NotesViewModel::class.java]
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
        settingsView = view.findViewById(R.id.bottom_sheet_settings)
        bottomAddSheetBehavior = BottomSheetBehavior.from(noteCreationView)
        settingsSheetBehavior = BottomSheetBehavior.from(settingsView)
        notesList.layoutManager = LinearLayoutManager(context)
        notesList.adapter = adapter
        notesList.addItemDecoration(NotesItemDecoration(8f.toPx(resources),
                8f.toPx(resources),
                16f.toPx(resources),
                16f.toPx(resources)))

        setupEditViewModel()
        setupNotesView()
        noteCreationView.apply {
            descriptionListener = { editNoteViewModel.setDescription(it) }
            titleListener = { editNoteViewModel.setTitle(it) }
            saveListener = { editNoteViewModel.saveNote() }
            clearDateListener = { editNoteViewModel.clearDate() }
            clearNoteListener = { editNoteViewModel.clearCurrentNote() }
            fullScreenListener = { openFullScreen() }
            formattedHintProducer = { editNoteViewModel.getFormattedHint(it) }
            calendarDialogListener = { editNoteViewModel.openDateDialog() }
        }
        fabAdd.setOnClickListener { editNoteViewModel.setNoteCreationOpen(true) }
        notesList.setOnTouchListener { _: View, motionEvent: MotionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN)
                bottomAddSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            false
        }
        bottomAddSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                bottomAppBar.visibility = View.VISIBLE
                if (slideOffset <= 1f && slideOffset >= -1f) {
                    bottomAppBar.animate().translationY(slideOffset * bottomAppBar.height).setDuration(0).start()
                    if (1f - slideOffset == 1f) {
                        fabAdd.show()
                    } else {
                        fabAdd.hide()
                    }
                }
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN || newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    hideKeyboard(bottomSheet)
                    editNoteViewModel.setNoteCreationOpen(false)
                }
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    hideKeyboard(bottomSheet)
                    editNoteViewModel.setNoteCreationOpen(true)
                }
            }
        })
        if (editNoteViewModel.noteCreationOpen().value == true) {
            bottomAddSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomAppBar.hideOnScroll = false
            bottomAppBar.visibility = View.INVISIBLE
            fabAdd.hide()
        }
        val noteContent = noteCreationView.findViewById<NestedScrollView>(R.id.note_content)

        noteContent.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_MOVE) {
                if (noteContent.scrollY != 0) {
                    (bottomAddSheetBehavior as LockableBottomSheetBehavior).swipeEnabled = false
                }
            }
            (bottomAddSheetBehavior as LockableBottomSheetBehavior).swipeEnabled = true
            return@setOnTouchListener false
        }
        bottomAppBar.replaceMenu(R.menu.settings_menu)
        bottomAppBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.app_bar_settings -> {
                    settingsSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    true
                }
                else -> false
            }
        }
    }

    private fun openFullScreen() {
        hideKeyboardListener(noteCreationView.findFocus()) {
            val nextFragment = EditNoteFragment.initMe(scrollY = noteCreationView.noteContent.scrollY)


            nextFragment.sharedElementEnterTransition = TransitionSet()
                    .apply {
                        addTransition(TransitionInflater.from(context).inflateTransition(android.R.transition.move))
                        startDelay = 0
                        duration = resources.getInteger(R.integer.animation_duration).toLong()
                    }

            val transaction = this.fragmentManager
                    ?.beginTransaction()
                    ?.replace(R.id.notes_layout_container, nextFragment)
                    ?.addSharedElement(noteCreationView.noteContent, noteCreationView.noteContent.transitionName)
                    ?.addSharedElement(noteCreationView.background, noteCreationView.background.transitionName)
                    ?.addSharedElement(noteCreationView.saveNoteButton, noteCreationView.saveNoteButton.transitionName)
                    ?.addSharedElement(noteCreationView.fullScreenButton, noteCreationView.fullScreenButton.transitionName)
                    ?.addSharedElement(noteCreationView.dateChip, noteCreationView.dateChip.transitionName)
            if (noteCreationView.deleteButton.visibility == View.VISIBLE && noteCreationView.calendarButton.visibility == View.VISIBLE) {
                transaction?.addSharedElement(noteCreationView.deleteButton, noteCreationView.deleteButton.transitionName)
                        ?.addSharedElement(noteCreationView.calendarButton, noteCreationView.calendarButton.transitionName)
            }
            transaction?.addToBackStack(null)
            transaction?.commit()

        }
    }

    private fun setupEditViewModel() {
        editNoteViewModel = activity?.run { ViewModelProviders.of(this, viewModelFactory)[NoteEditViewModel::class.java] } ?: throw NullPointerException()
        editNoteViewModel.currentNote().observe(viewLifecycleOwner, Observer { noteCreationView.updateNoteData(it) })
        editNoteViewModel.showNoteDeleted().observe(viewLifecycleOwner, Observer {
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
                                    if (event == DISMISS_EVENT_ACTION) {
                                        bottomAppBar.visibility = View.VISIBLE
                                        bottomAppBar.hideOnScroll = true
                                        bottomAppBar.animate().translationY(0f).setDuration(200).start()
                                        fabAdd.show()
                                    } else if (event == DISMISS_EVENT_SWIPE || event == DISMISS_EVENT_TIMEOUT) {
                                        bottomAppBar.visibility = View.VISIBLE
                                        bottomAppBar.hideOnScroll = true
                                        bottomAppBar.animate().translationY(0f).setDuration(200).start()
                                        editNoteViewModel.onCurrentNoteCleared()
                                        fabAdd.show()
                                    }
                                }
                            })
                            .setAction(R.string.undo) { editNoteViewModel.undoClearCurrentNote() }
                            .setActionTextColor(ContextCompat.getColor(this.context, R.color.colorAction))
                    val layout = snackBar.view as Snackbar.SnackbarLayout
                    val textView = layout.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
                    textView.setTextColor(ContextCompat.getColor(this.context, R.color.white))
                    snackBar.show()
                }
            }
        })
        editNoteViewModel.saveResult().observe(viewLifecycleOwner, Observer {
            if (it?.status == Status.ERROR) {
                context?.run { Toast.makeText(this, getString(R.string.error_note_create), Toast.LENGTH_SHORT).show() }
            }
        })
        editNoteViewModel.dateDialog().observe(viewLifecycleOwner, Observer {
            if (it != null) {
                context?.run {
                    val dialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, y, m, d -> editNoteViewModel.setDate(y, m, d) },
                            it.get(Calendar.YEAR),
                            it.get(Calendar.MONTH),
                            it.get(Calendar.DAY_OF_MONTH))
                    dialog.setOnDismissListener { editNoteViewModel.closeDateDialog() }
                    dialog.show()
                }
            }
        })
        editNoteViewModel.noteCreationOpen().observe(viewLifecycleOwner, Observer {
            if (it == false) {
                bottomAddSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            } else if (it == true) {
                bottomAddSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        })
        editNoteViewModel.openFullScreenCreation().observe(viewLifecycleOwner, Observer {
            if (it == true) {
                val nextFragment = EditNoteFragment.initMe(forceBackButtonVisible = true)
                val exitFade = Fade(Fade.OUT).apply {
                    duration = resources.getInteger(R.integer.animation_duration).toLong()
                }
                val enterSlide = Slide(Gravity.END).apply {
                    duration = resources.getInteger(R.integer.animation_duration).toLong()
                }
                nextFragment.enterTransition = enterSlide
                exitTransition = exitFade

                val transaction = fragmentManager
                        ?.beginTransaction()
                        ?.replace(R.id.notes_layout_container, nextFragment)
                        ?.addToBackStack(null)
                transaction?.commit()
            }
        })
    }

    private fun setupNotesView() {
        notesViewModel.getNotes().observe(viewLifecycleOwner, Observer {
            val list = it.data
            if (it.error == null && list != null) {
                onNotesLoaded(list)
            } else {
                error("loadNotes", it.error)
            }
        })
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
}
