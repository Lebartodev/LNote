package com.lebartodev.lnotes.list

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.*
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.lebartodev.core.base.BaseFragment
import com.lebartodev.core.db.entity.Note
import com.lebartodev.lnote.archive.ArchiveFragment
import com.lebartodev.lnote.show.ShowNoteFragment
import com.lebartodev.lnote.utils.ui.*
import javax.inject.Inject

class NotesFragment : BaseFragment() {
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var bottomAppBar: BottomAppBar
    private lateinit var notesList: RecyclerView
    private lateinit var noteCreationView: NoteCreationView

    private lateinit var bottomAddSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private var activeBottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>? = null
    private lateinit var settingsSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private var canScrollNotes = true
    private val adapter = NotesAdapter { note, sharedViews ->
        note.id?.run {
            val nextFragment = ShowNoteFragment.initMe(this@run)
            val transition = TransitionSet()
            transition.addTransition(ChangeTransform())
            transition.addTransition(ChangeImageTransform())
            transition.addTransition(ChangeBounds())
            transition.addTransition(ChangeClipBounds())
            transition.addTransition(CardExpandTransition())
            transition.interpolator = LinearOutSlowInInterpolator()
            transition.duration = resources.getInteger(R.integer.animation_duration).toLong()
            nextFragment.sharedElementEnterTransition = transition

            transition.addListener(object : TransitionListenerAdapter() {
                override fun onTransitionStart(transition: Transition) {
                    super.onTransitionStart(transition)
                    canScrollNotes = false
                }

                override fun onTransitionEnd(transition: Transition) {
                    super.onTransitionEnd(transition)
                    canScrollNotes = true
                }
            })
            sharedElementReturnTransition = transition

            fragmentManager?.beginTransaction()?.run {
                setReorderingAllowed(true)
                setCustomAnimations(0, R.anim.fade_out, 0, R.anim.fade_out)
                sharedViews.forEach { addSharedElement(it, it.transitionName) }
                replace(R.id.container, nextFragment)
                addToBackStack(ShowNoteFragment.BACK_STACK_TAG)
                commit()
            }
        }
    }

    @Inject
    lateinit var viewModelFactory: ListNotesViewModelFactory
    private lateinit var notesViewModel: NotesViewModel
    private lateinit var editNoteViewModel: NoteEditViewModel

    private var isSnackBarVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.run {
            if ((application as LNoteApplication).notesComponent == null) {
                (application as LNoteApplication).notesComponent = DaggerNotesComponent.builder()
                        .appComponent(LNoteApplication[this].appComponent)
                        .context(this)
                        .build()
            }
            (activity?.application as LNoteApplication).notesComponent?.inject(this@NotesFragment)
        }
        notesViewModel = ViewModelProvider(this, viewModelFactory)[NotesViewModel::class.java]
        editNoteViewModel = ViewModelProvider(this, viewModelFactory)[NoteEditViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        postponeEnterTransition()
        return inflater.inflate(R.layout.fragment_notes, container, false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fabAdd = view.findViewById(R.id.fab_add)
        bottomAppBar = view.findViewById(R.id.bottom_app_bar)
        notesList = view.findViewById(R.id.notes_list)
        noteCreationView = view.findViewById(R.id.bottom_sheet_add)
        bottomAddSheetBehavior = BottomSheetBehavior.from(noteCreationView)
        settingsSheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.bottom_sheet_settings))
        notesList.layoutManager = object : LinearLayoutManager(context) {
            override fun canScrollVertically(): Boolean {
                return canScrollNotes
            }
        }
        notesList.adapter = adapter
        notesList.addItemDecoration(NotesItemDecoration(8f.toPx(resources),
                8f.toPx(resources),
                16f.toPx(resources),
                16f.toPx(resources)))

        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT.or(ItemTouchHelper.RIGHT)) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                editNoteViewModel.deleteNote(viewHolder.itemId)
            }
        }
        val touchHelper = ItemTouchHelper(simpleItemTouchCallback)
        touchHelper.attachToRecyclerView(notesList)

        setupEditViewModel()
        setupNotesViewModel()

        initBottomSheets()
        initBottomAppBar()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initBottomSheets() {
        val callback = object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) = onSlideBottomSheet(slideOffset)

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN || newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    hideKeyboard(bottomSheet)
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    activeBottomSheetBehavior = bottomAddSheetBehavior
                    hideKeyboard(bottomSheet)
                }
            }
        }
        settingsSheetBehavior.setBottomSheetCallback(callback)
        bottomAddSheetBehavior.setBottomSheetCallback(callback)

        noteCreationView.apply {
            descriptionListener = { editNoteViewModel.setDescription(it) }
            titleListener = { editNoteViewModel.setTitle(it) }
            saveListener = {
                editNoteViewModel.saveNote()
                closeNoteCreation()
            }
            clearDateListener = { editNoteViewModel.clearDate() }
            clearNoteListener = {
                editNoteViewModel.deleteEditedNote()
                closeNoteCreation()
            }
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
        fragmentManager?.findFragmentByTag(TAG_CALENDAR_DIAlOG)?.run {
            (this as SelectDateFragment).listener = DatePickerDialog.OnDateSetListener { _, y, m, d -> editNoteViewModel.setDate(y, m, d) }
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
                R.id.app_bar_archive -> {
                    fragmentManager?.beginTransaction()?.run {
                        setReorderingAllowed(true)
                        setCustomAnimations(0, R.anim.fade_out, 0, R.anim.fade_out)
                        replace(R.id.container, ArchiveFragment())
                        addToBackStack(null)
                        commit()
                    }

                    true
                }
                else -> false
            }
        }
    }

    private fun setupEditViewModel() {
        editNoteViewModel.forceEditCurrentNote().observe(viewLifecycleOwner, Observer {
            if (it == true) {
                fabAdd.callOnClick()
            }
        })
        editNoteViewModel.currentNote().observe(viewLifecycleOwner, Observer { noteCreationView.updateNoteData(it) })

        editNoteViewModel.saveResult().observe(viewLifecycleOwner, Observer {
            if (it == true) {
                //onNoteSaved()
            }
        })

        editNoteViewModel.error().observe(viewLifecycleOwner, Observer {
            val text = when (it) {
                is com.lebartodev.lnote.utils.exception.DeleteNoteException -> R.string.error_note_create
                else -> R.string.error_common
            }
            context?.run { Toast.makeText(this, getString(text), Toast.LENGTH_SHORT).show() }
        })
        editNoteViewModel.bottomPanelEnabled().observe(viewLifecycleOwner, Observer {
            if (it == true) {
                fabAdd.setOnClickListener {
                    openNoteCreation()
                }
                fabAdd.transitionName = null
                noteCreationView.transitionName = resources.getString(R.string.note_container_transition_name, "local")
            } else if (it == false) {
                fabAdd.setOnClickListener {
                    openFullScreen(false)
                }
                noteCreationView.transitionName = null
                fabAdd.transitionName = resources.getString(R.string.note_container_transition_name, "local")
            }
        })
    }

    private fun setupNotesViewModel() {
        editNoteViewModel.deleteResult().observe(viewLifecycleOwner, Observer {
            if (it == true){

            }
               // onNoteDeleted()
        })
        notesViewModel.getNotes().observe(viewLifecycleOwner, Observer {
            onNotesLoaded(it)
        })
    }

    private fun openFullScreen(fromBottomSheet: Boolean) {
        hideKeyboardListener(noteCreationView.findFocus()) {

            val nextFragment = EditNoteFragment.initMe(forceBackButtonVisible = !fromBottomSheet,
                    scrollY = noteCreationView.getContentScroll())
                    .apply {
                        sharedElementEnterTransition = TransitionSet()
                                .apply {
                                    if (fromBottomSheet) {
                                        addTransition(TransitionInflater.from(this@NotesFragment.context).inflateTransition(android.R.transition.move))
                                        addTransition(CardExpandTransition())
                                    } else
                                        addTransition(FabTransition())
                                    duration = this@NotesFragment.resources.getInteger(R.integer.animation_duration).toLong()
                                    interpolator = LinearOutSlowInInterpolator()
                                }
                    }

            fragmentManager?.beginTransaction()?.run {
                setReorderingAllowed(true)
                if (!fromBottomSheet) {
                    setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                    addSharedElement(fabAdd, fabAdd.transitionName)
                } else {
                    setCustomAnimations(0, R.anim.fade_out, 0, R.anim.fade_out)
                    noteCreationView.getSharedViews()
                            .forEach { addSharedElement(it, it.transitionName) }
                }
                replace(R.id.container, nextFragment)
                addToBackStack(null)
                commit()
            }
        }
    }

    private fun openNoteCreation() {
        bottomAddSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun closeNoteCreation() {
        bottomAddSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun openSettings() {
        settingsSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun onSlideBottomSheet(slideOffset: Float, withAnimation: Boolean = true) {
        if (!isSnackBarVisible) {
            if (slideOffset <= 0f) {
                setBottomAppBarVisibility(true, withAnimation)
            } else if (slideOffset == 1f) {
                setBottomAppBarVisibility(false, withAnimation)
            }
        }
    }

    private fun setBottomAppBarVisibility(visible: Boolean, withAnimation: Boolean = true) {
        val translationY = if (visible) 0f else bottomAppBar.height.toFloat()
        bottomAppBar.animate().cancel()
        bottomAppBar.animate()
                .setInterpolator(LinearOutSlowInInterpolator())
                .translationY(translationY)
                .setDuration(if (withAnimation) resources.getInteger(R.integer.animation_duration).toLong() / 2 else 0)
                .start()
        if (visible) {
            fabAdd.show()
        } else {
            fabAdd.hide()
        }
    }

    private fun onNotesLoaded(notes: List<Note>) {
        if (adapter.data.isEmpty()) {
            adapter.updateData(notes)
            startPostponedEnterTransition()
        } else
            notesList.post {
                adapter.updateData(notes)
                startPostponedEnterTransition()
            }
    }

    override fun onResume() {
        super.onResume()
//        activity?.run {
//            if (this is EditorEventContainer) {
//                when (this.popEditorEvent()) {
//                    EditorEvent.SAVE -> onNoteSaved()
//                    EditorEvent.DELETE -> onNoteDeleted()
//                    else -> {
//                    }
//                }
//            }
//        }
    }

//    override fun onNoteDeleted() {
//        bottomAddSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
//        view?.run {
//            Snackbar.make(this, R.string.note_deleted, Snackbar.LENGTH_LONG)
//                    .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
//                        override fun onShown(transientBottomBar: Snackbar?) {
//                            super.onShown(transientBottomBar)
//                            setBottomAppBarVisibility(false, withAnimation = false)
//                            bottomAppBar.hideOnScroll = false
//                            isSnackBarVisible = true
//                        }
//
//                        override fun onDismissed(
//                                transientBottomBar: Snackbar?,
//                                event: Int
//                        ) {
//                            super.onDismissed(transientBottomBar, event)
//                            isSnackBarVisible = false
//                            setBottomAppBarVisibility(true, withAnimation = true)
//                            bottomAppBar.hideOnScroll = true
//                        }
//                    })
//                    .setAction(R.string.undo) {
//                        editNoteViewModel.restoreLastNote()
//                    }
//                    .setActionTextColor(ContextCompat.getColor(context, R.color.colorAction))
//                    .apply {
//                        val layout = view as Snackbar.SnackbarLayout
//                        val textView = layout.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
//                        textView.setTextColor(ContextCompat.getColor(this@run.context, R.color.white))
//                    }
//                    .show()
//        }
//    }

//    override fun onNoteSaved() {
//        bottomAddSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
//    }


    companion object {
        const val TAG = "NotesFragment"
        private const val TAG_CALENDAR_DIAlOG = "TAG_CALENDAR_DIAlOG"
    }
}
