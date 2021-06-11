package com.lebartodev.lnotes.list

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.NestedScrollView
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.lebartodev.core.base.BaseFragment
import com.lebartodev.core.db.entity.Note
import com.lebartodev.core.di.utils.AppComponentProvider
import com.lebartodev.lnote.archive.ArchiveFragment
import com.lebartodev.lnote.show.ShowNoteFragment
import com.lebartodev.lnote.utils.ui.*
import com.lebartodev.lnotes.list.databinding.FragmentNotesBinding
import com.lebartodev.lnotes.list.di.DaggerListComponent
import javax.inject.Inject

class NotesFragment : BaseFragment() {
    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!

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

            parentFragmentManager.beginTransaction().run {
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
    private val notesViewModel: NotesViewModel by lazy { ViewModelProvider(this, viewModelFactory)[NotesViewModel::class.java] }
    private var isSnackBarVisible = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerListComponent.builder()
                .appComponent((context.applicationContext as AppComponentProvider).provideAppComponent())
                .context(context)
                .build()
                .inject(this)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        postponeEnterTransition()
        _binding = FragmentNotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomAddSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetAdd)
        settingsSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetSettings)
        binding.notesList.layoutManager = object : LinearLayoutManager(context) {
            override fun canScrollVertically(): Boolean {
                return canScrollNotes
            }
        }
        binding.notesList.adapter = adapter
        binding.notesList.addItemDecoration(
                NotesItemDecoration(
                        8f.toPx(resources),
                        8f.toPx(resources),
                        16f.toPx(resources),
                        16f.toPx(resources)
                )
        )

        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT.or(ItemTouchHelper.RIGHT)) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                notesViewModel.deleteNote(viewHolder.itemId)
            }
        }
        val touchHelper = ItemTouchHelper(simpleItemTouchCallback)
        touchHelper.attachToRecyclerView(binding.notesList)

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

        binding.bottomSheetAdd.apply {
            saveListener = {
                bottomAddSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
            fullScreenListener = { openFullScreen(true) }
        }
        binding.notesList.setOnTouchListener { _: View, motionEvent: MotionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN)
                activeBottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
            false
        }

        val noteContent = binding.bottomSheetAdd.findViewById<NestedScrollView>(R.id.note_content)
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
        binding.bottomAppBar.replaceMenu(R.menu.settings_menu)
        binding.bottomAppBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.app_bar_settings -> {
                    openSettings()
                    true
                }
                R.id.app_bar_archive -> {
                    parentFragmentManager.beginTransaction().run {
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
//        editNoteViewModel.forceEditCurrentNote().observe(viewLifecycleOwner, Observer {
//            if (it == true) {
//                fabAdd.callOnClick()
//            }
//        })
//        editNoteViewModel.currentNote().observe(viewLifecycleOwner, Observer { noteCreationView.updateNoteData(it) })
//
    }

    private fun setupNotesViewModel() {
        notesViewModel.error().observe(viewLifecycleOwner, {
            val text = when (it) {
                is com.lebartodev.lnote.utils.exception.DeleteNoteException -> R.string.error_note_create
                else -> R.string.error_common
            }
            context?.run { Toast.makeText(this, getString(text), Toast.LENGTH_SHORT).show() }
        })
        notesViewModel.getNotes().observe(viewLifecycleOwner, {
            onNotesLoaded(it)
        })
        notesViewModel.bottomPanelEnabled().observe(viewLifecycleOwner, {
            if (it == true) {
                binding.fabAdd.setOnClickListener {
                    openNoteCreation()
                }
                binding.fabAdd.transitionName = null
                binding.bottomSheetAdd.transitionName = resources.getString(R.string.note_container_transition_name, "local")
            } else if (it == false) {
                binding.fabAdd.setOnClickListener {
                    openFullScreen(false)
                }
                binding.bottomSheetAdd.transitionName = null
                binding.fabAdd.transitionName = resources.getString(R.string.note_container_transition_name, "local")
            }
        })
    }

    private fun openFullScreen(fromBottomSheet: Boolean) {
        hideKeyboardListener(binding.bottomSheetAdd.findFocus()) {

            val nextFragment = com.lebartodev.lnote.edit.EditNoteFragment.initMe(
                    forceBackButtonVisible = !fromBottomSheet,
                    scrollY = binding.bottomSheetAdd.getContentScroll()
            )
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

            parentFragmentManager.beginTransaction().run {
                setReorderingAllowed(true)
                if (!fromBottomSheet) {
                    setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                    addSharedElement(binding.fabAdd, binding.fabAdd.transitionName)
                } else {
                    setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                    binding.bottomSheetAdd.getSharedViews().forEach { addSharedElement(it, it.transitionName) }
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
        val translationY = if (visible) 0f else binding.bottomAppBar.height.toFloat()
        binding.bottomAppBar.animate().cancel()
        binding.bottomAppBar.animate()
                .setInterpolator(LinearOutSlowInInterpolator())
                .translationY(translationY)
                .setDuration(if (withAnimation) resources.getInteger(R.integer.animation_duration).toLong() / 2 else 0)
                .start()
        if (visible) {
            binding.fabAdd.show()
        } else {
            binding.fabAdd.hide()
        }
    }

    private fun onNotesLoaded(notes: List<Note>) {
        if (adapter.data.isEmpty()) {
            adapter.updateData(notes)
            startPostponedEnterTransition()
        } else
            binding.notesList.post {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "NotesFragment"
        private const val TAG_CALENDAR_DIAlOG = "TAG_CALENDAR_DIAlOG"
    }
}
