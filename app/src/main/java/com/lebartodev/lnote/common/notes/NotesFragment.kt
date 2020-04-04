package com.lebartodev.lnote.common.notes

import android.animation.Animator
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
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
import com.lebartodev.lnote.common.LNoteApplication
import com.lebartodev.lnote.common.details.ShowNoteFragment
import com.lebartodev.lnote.common.edit.EditNoteFragment
import com.lebartodev.lnote.common.edit.NoteCreationView
import com.lebartodev.lnote.common.edit.NoteEditViewModel
import com.lebartodev.lnote.common.settings.SettingsBottomView
import com.lebartodev.lnote.data.entity.Note
import com.lebartodev.lnote.data.entity.Status
import com.lebartodev.lnote.di.notes.DaggerNotesComponent
import com.lebartodev.lnote.utils.LNoteViewModelFactory
import com.lebartodev.lnote.utils.error
import com.lebartodev.lnote.utils.ui.*
import javax.inject.Inject

class NotesFragment : BaseFragment() {
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var bottomAppBar: BottomAppBar
    private lateinit var notesList: RecyclerView
    private lateinit var noteCreationView: NoteCreationView

    private lateinit var bottomAddSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private var activeBottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>? = null
    private var settingsSheetBehavior: BottomSheetBehavior<ConstraintLayout>? = null
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

    @Inject
    lateinit var viewModelFactory: LNoteViewModelFactory
    private lateinit var notesViewModel: NotesViewModel
    private lateinit var editNoteViewModel: NoteEditViewModel
    private lateinit var notesContent: CoordinatorLayout

    private var isSnackBarVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context?.run {
            DaggerNotesComponent.builder()
                    .appComponent(LNoteApplication[this].appComponent)
                    .context(this)
                    .build()
                    .inject(this@NotesFragment)
        }
        notesViewModel = ViewModelProviders.of(this, viewModelFactory)[NotesViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_notes, container, false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notesContent = view.findViewById(R.id.notes_content)
        fabAdd = view.findViewById(R.id.fab_add)
        bottomAppBar = view.findViewById(R.id.bottom_app_bar)
        notesList = view.findViewById(R.id.notes_list)
        noteCreationView = view.findViewById(R.id.bottom_sheet_add)
        bottomAddSheetBehavior = BottomSheetBehavior.from(noteCreationView)
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
            fullScreenListener = { openFullScreen(true) }
            calendarDialogListener = {
                fragmentManager?.run {
                    val dialog = SelectDateFragment.initMe(it)
                    dialog.listener = DatePickerDialog.OnDateSetListener { _, y, m, d -> editNoteViewModel.setDate(y, m, d) }
                    dialog.show(this, TAG_CALENDAR_DIAlOG)
                }
            }
        }
        notesList.setOnTouchListener { _: View, motionEvent: MotionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN)
                activeBottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
            false
        }

        initBottomAddSheetBehavior()
        initBottomAppBar()

        if (savedInstanceState?.getBoolean(SETTINGS_OPEN_STATE) == true) {
            openSettings()
        }
        if (savedInstanceState?.getBoolean(NOTE_CREATION_OPEN) == true) {
            openNoteCreation()
        }
        fragmentManager?.findFragmentByTag(TAG_CALENDAR_DIAlOG)?.run {
            (this as SelectDateFragment).listener = DatePickerDialog.OnDateSetListener { _, y, m, d -> editNoteViewModel.setDate(y, m, d) }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initBottomAddSheetBehavior() {
        bottomAddSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) = onSlideBottomSheet(slideOffset)

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN || newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    hideKeyboard(bottomSheet)
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    activeBottomSheetBehavior = bottomAddSheetBehavior
                    hideKeyboard(bottomSheet)
                }
            }
        })
        val noteContent = noteCreationView.findViewById<NestedScrollView>(R.id.note_content)
        noteContent.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_MOVE) {
                if (noteContent.scrollY != 0) {
                    (bottomAddSheetBehavior as LockableBottomSheetBehavior).swipeEnabled = false
                }
            }
            (bottomAddSheetBehavior as LockableBottomSheetBehavior).swipeEnabled = true
            return@setOnTouchListener false
        }
    }

    private fun initBottomAppBar() {
        bottomAppBar.replaceMenu(R.menu.settings_menu)
        bottomAppBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.app_bar_settings -> {
                    openSettings()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupEditViewModel() {
        editNoteViewModel = activity?.run { ViewModelProviders.of(this, viewModelFactory)[NoteEditViewModel::class.java] } ?: throw NullPointerException()
        editNoteViewModel.currentNote().observe(viewLifecycleOwner, Observer { noteCreationView.updateNoteData(it) })
        editNoteViewModel.showNoteDeleted().observe(viewLifecycleOwner, Observer {
            if (it == true) {
                view?.run {
                    val snackBar = Snackbar.make(this, R.string.note_deleted, Snackbar.LENGTH_LONG)
                            .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                                override fun onShown(transientBottomBar: Snackbar?) {
                                    super.onShown(transientBottomBar)
                                    onSlideBottomSheet(1f)
                                    isSnackBarVisible = true
                                }

                                override fun onDismissed(transientBottomBar: Snackbar?,
                                                         event: Int) {
                                    super.onDismissed(transientBottomBar, event)
                                    isSnackBarVisible = false
                                    if (event == DISMISS_EVENT_SWIPE || event == DISMISS_EVENT_TIMEOUT) {
                                        editNoteViewModel.onCurrentNoteCleared()
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
        editNoteViewModel.bottomPanelEnabled().observe(viewLifecycleOwner, Observer {
            if (it == true) {
                fabAdd.setOnClickListener {
                    openNoteCreation()
                }
            } else if (it == false) {
                openFullScreen(false)
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(SETTINGS_OPEN_STATE, settingsSheetBehavior != null)
        outState.putBoolean(NOTE_CREATION_OPEN, bottomAddSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN)
    }

    private fun openFullScreen(withTransaction: Boolean) {
        hideKeyboardListener(noteCreationView.findFocus()) {

            val nextFragment = EditNoteFragment.initMe(forceBackButtonVisible = !withTransaction, scrollY = noteCreationView.noteContent.scrollY)

            if (!withTransaction) {
                val exitFade = Fade(Fade.OUT).apply {
                    duration = resources.getInteger(R.integer.animation_duration).toLong()
                }
                val enterSlide = Slide(Gravity.END).apply {
                    duration = resources.getInteger(R.integer.animation_duration).toLong()
                }
                nextFragment.enterTransition = enterSlide
                exitTransition = exitFade
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
            if (withTransaction) {
                transaction?.addSharedElement(noteCreationView.noteContent, noteCreationView.noteContent.transitionName)
                        ?.addSharedElement(noteCreationView.background, noteCreationView.background.transitionName)
                        ?.addSharedElement(noteCreationView.saveNoteButton, noteCreationView.saveNoteButton.transitionName)
                        ?.addSharedElement(noteCreationView.fullScreenButton, noteCreationView.fullScreenButton.transitionName)
                        ?.addSharedElement(noteCreationView.dateChip, noteCreationView.dateChip.transitionName)
            }
            if (withTransaction && noteCreationView.deleteButton.visibility == View.VISIBLE && noteCreationView.calendarButton.visibility == View.VISIBLE) {
                transaction?.addSharedElement(noteCreationView.deleteButton, noteCreationView.deleteButton.transitionName)
                        ?.addSharedElement(noteCreationView.calendarButton, noteCreationView.calendarButton.transitionName)
            }
            transaction?.addToBackStack(null)
            transaction?.commit()
        }
    }

    private fun openNoteCreation() {
        bottomAddSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun openSettings() {
        if (settingsSheetBehavior == null) {
            val settingsView = SettingsBottomView(context)
            val params = CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT)

            settingsView.elevation = 24f.toPx(resources)
            settingsView.setPadding(0, 4f.toPx(resources).toInt(), 0, 0)
            settingsSheetBehavior = BottomSheetBehavior<ConstraintLayout>().apply {
                isHideable = true
                peekHeight = 0
                setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onSlide(view: View, slideOffset: Float) {
                        onSlideBottomSheet(slideOffset)
                    }

                    override fun onStateChanged(view: View, state: Int) {
                        if (state == BottomSheetBehavior.STATE_COLLAPSED) {
                            notesContent.removeView(settingsView)
                            setBottomSheetCallback(null)
                            settingsSheetBehavior = null
                        } else if (state == BottomSheetBehavior.STATE_EXPANDED) {
                            activeBottomSheetBehavior = settingsSheetBehavior
                        }
                    }
                })
            }
            params.behavior = settingsSheetBehavior
            settingsView.layoutParams = params

            settingsView.onLayout { settingsSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED }
            notesContent.addView(settingsView)
        } else {
            settingsSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun onSlideBottomSheet(slideOffset: Float) {
        if (!isSnackBarVisible) {
            bottomAppBar.visibility = View.VISIBLE
            if (slideOffset <= 1f && slideOffset >= -1f) {
                bottomAppBar.animate().translationY(slideOffset * bottomAppBar.height).setDuration(0).start()
                if (1f - slideOffset == 1f) {
                    fabAdd.show()
                } else {
                    fabAdd.hide()
                }
            }
            bottomAppBar.hideOnScroll = slideOffset == 1f
        }
    }

    private fun onNotesLoaded(notes: List<Note>) {
        adapter.data = notes
    }

    companion object {
        const val TAG = "NotesFragment"
        private const val SETTINGS_OPEN_STATE = "SETTINGS_OPEN_STATE"
        private const val NOTE_CREATION_OPEN = "NOTE_CREATION_OPEN"
        private const val TAG_CALENDAR_DIAlOG = "TAG_CALENDAR_DIAlOG"
    }
}
