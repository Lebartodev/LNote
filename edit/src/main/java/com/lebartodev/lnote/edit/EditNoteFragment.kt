package com.lebartodev.lnote.edit

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.lebartodev.core.base.BaseFragment
import com.lebartodev.core.data.NoteData
import com.lebartodev.lnote.utils.extensions.animateSlideBottomVisibility
import com.lebartodev.lnote.utils.extensions.animateSlideTopVisibility
import com.lebartodev.lnote.utils.extensions.formattedHint
import com.lebartodev.lnote.utils.extensions.onLayout
import com.lebartodev.lnote.utils.ui.DateChip
import com.lebartodev.lnote.utils.ui.SelectDateFragment
import com.lebartodev.lnote.utils.ui.toPx
import java.util.Calendar
import javax.inject.Inject

class EditNoteFragment : BaseFragment() {
    private lateinit var descriptionTextView: TextView
    private lateinit var titleTextView: TextView
    private lateinit var fullScreenButton: ImageButton
    private lateinit var deleteButton: ImageButton
    private lateinit var saveNoteButton: MaterialButton
    private lateinit var calendarButton: ImageButton
    private lateinit var dateChip: DateChip
    private lateinit var backButton: ImageButton
    private lateinit var actionBarTitleTextView: TextView
    private lateinit var noteContent: NestedScrollView
    private var noteId: Long? = null
    private var scroll: Int? = null
    private val noteObserver: Observer<NoteData> = Observer { noteData ->
        titleTextView.removeTextChangedListener(titleTextWatcher)
        descriptionTextView.removeTextChangedListener(descriptionTextWatcher)

        val description = noteData.text ?: ""
        val title = noteData.title
        val time = noteData.date

        if (description != titleTextView.hint) {
            if (description.isNotEmpty()) {
                titleTextView.hint = description.formattedHint()
            } else {
                titleTextView.hint = context?.getString(R.string.title_hint)
            }
        }

        if (titleTextView.text.toString() != title) {
            titleTextView.text = title
        }
        if (descriptionTextView.text.toString() != description) {
            descriptionTextView.text = description
        }
        if (isSharedAnimationEnd)
            dateChip.setDateAnimated(time)
        else
            dateChip.setDate(time)
        if (scroll != null && !noteData.text.isNullOrEmpty()) {
            noteContent.post {
                noteContent.scrollTo(0, scroll ?: 0)
                startPostponedEnterTransition()
                scroll = null
            }
        } else if (noteId != null && noteData.id == noteId) {
            startPostponedEnterTransition()
        } else if (noteId == null) {
            startPostponedEnterTransition()
        }
        actionBarTitleTextView.hint = titleTextView.hint
        actionBarTitleTextView.text = titleTextView.text
        titleTextView.addTextChangedListener(titleTextWatcher)
        descriptionTextView.addTextChangedListener(descriptionTextWatcher)
    }

    @Inject
    lateinit var viewModelFactory: EditNoteViewModelFactory
    private lateinit var viewModel: NoteEditViewModel
    private val descriptionTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            viewModel.setDescription(s?.toString() ?: "")
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }
    private val titleTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            viewModel.setTitle(s?.toString() ?: "")
            actionBarTitleTextView.text = s?.toString() ?: ""
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postponeEnterTransition()
        context?.run {
//            if ((activity?.application as LNoteApplication).notesComponent == null) {
//                (activity?.application as LNoteApplication).notesComponent = DaggerNotesComponent.builder()
//                        .appComponent(LNoteApplication[this].appComponent)
//                        .context(this)
//                        .build()
//            }
//            (activity?.application as LNoteApplication).notesComponent?.inject(this@EditNoteFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_note, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.run {
            if (containsKey(EXTRA_SCROLL))
                scroll = getInt(EXTRA_SCROLL)
            if (containsKey(EXTRA_ID))
                noteId = getLong(EXTRA_ID)
        }
        super.onViewCreated(view, savedInstanceState)
        titleTextView = view.findViewById(R.id.text_title)
        descriptionTextView = view.findViewById(R.id.text_description)
        fullScreenButton = view.findViewById(R.id.full_screen_button)
        saveNoteButton = view.findViewById(R.id.save_button)
        deleteButton = view.findViewById(R.id.delete_button)
        dateChip = view.findViewById(R.id.date_chip)
        calendarButton = view.findViewById(R.id.calendar_button)
        backButton = view.findViewById(R.id.back_button)
        actionBarTitleTextView = view.findViewById(R.id.text_title_action_bar)
        noteContent = view.findViewById(R.id.note_content)

        view.transitionName = resources.getString(R.string.note_container_transition_name, noteId?.toString() ?: "local")
        noteContent.transitionName = resources.getString(R.string.note_content_transition_name, noteId?.toString() ?: "local")
        titleTextView.transitionName = resources.getString(R.string.note_title_transition_name, noteId?.toString() ?: "local")
        descriptionTextView.transitionName = resources.getString(R.string.note_description_transition_name, noteId?.toString() ?: "local")
        dateChip.transitionName = resources.getString(R.string.note_date_transition_name, noteId?.toString() ?: "local")

        val visibleTitleLimit = 56f.toPx(resources)
        noteContent.setOnScrollChangeListener { _: NestedScrollView?, _: Int, scrollY: Int, _: Int, oldScrollY: Int ->
            if (scrollY >= visibleTitleLimit && oldScrollY < visibleTitleLimit) {
                actionBarTitleTextView.animate().cancel()
                actionBarTitleTextView.animate().alpha(1f).start()
            } else if (scrollY < visibleTitleLimit && oldScrollY > visibleTitleLimit) {
                actionBarTitleTextView.animate().cancel()
                actionBarTitleTextView.animate().alpha(0f).start()
            }
        }

        descriptionTextView.addTextChangedListener(descriptionTextWatcher)
        titleTextView.addTextChangedListener(titleTextWatcher)
        if (noteId != null) {
            deleteButton.visibility = View.GONE
        } else {
            deleteButton.visibility = View.VISIBLE
            deleteButton.setOnClickListener {
                viewModel.deleteEditedNote()
            }
        }
        if (noteId != null || arguments?.getBoolean(EXTRA_BACK_BUTTON_VISIBLE) == true) {
            backButton.setOnClickListener {
                hideKeyboard()
                fragmentManager?.popBackStack()
            }
            backButton.visibility = View.VISIBLE
            fullScreenButton.visibility = View.GONE
        } else {
            backButton.visibility = View.GONE
            fullScreenButton.visibility = View.VISIBLE
            fullScreenButton.setOnClickListener { fragmentManager?.popBackStack() }
        }

        saveNoteButton.setOnClickListener {
            hideKeyboard()
            viewModel.currentNote().removeObserver(noteObserver)
            viewModel.saveNote()
        }

        calendarButton.setOnClickListener { openCalendarDialog() }
        dateChip.setOnClickListener { openCalendarDialog() }
        dateChip.setOnCloseIconClickListener { viewModel.clearDate() }
        setupEditViewModel()
        if (savedInstanceState == null)
            noteId?.run { viewModel.loadNote(this) }

        fragmentManager?.findFragmentByTag(TAG_CALENDAR_DIAlOG)?.run {
            (this as SelectDateFragment).listener = DatePickerDialog.OnDateSetListener { _, y, m, d -> viewModel.setDate(y, m, d) }
        }
    }

    override fun onStartSharedAnimation(sharedElementNames: MutableList<String>) {
        listOf(saveNoteButton, deleteButton, calendarButton)
            .filter { !sharedElementNames.contains(it.transitionName) }
            .forEach {
                when (it.transitionName) {
                    saveNoteButton.transitionName -> {
                        it.onLayout {
                            it.visibility = View.GONE
                            it.animateSlideBottomVisibility(true)
                        }
                    }
                    calendarButton.transitionName -> {
                        it.onLayout {
                            it.visibility = View.GONE
                            it.animateSlideTopVisibility(true)
                        }
                    }
                    deleteButton.transitionName -> {
                        if (noteId == null) {
                            it.onLayout {
                                it.visibility = View.GONE
                                it.animateSlideTopVisibility(true)
                            }
                        }
                    }
                }
            }
    }

    private fun setupEditViewModel() {
        viewModel = ViewModelProvider(this, viewModelFactory)[NoteEditViewModel::class.java]
        viewModel.saveResult().observe(viewLifecycleOwner, Observer {
//            if (it == true) {
//                (activity as EditorEventContainer).saveNote()
//                titleTextView.clearFocus()
//                descriptionTextView.clearFocus()
//                sharedElementReturnTransition = null
//                fragmentManager?.popBackStack()
//            }
        })
        viewModel.deleteResult().observe(viewLifecycleOwner, Observer {
//            if (it == true) {
//                (activity as EditorEventContainer).deleteNote()
//                titleTextView.clearFocus()
//                descriptionTextView.clearFocus()
//                sharedElementReturnTransition = null
//                if (noteId == null) {
//                    fragmentManager?.popBackStack()
//                } else {
//                    fragmentManager?.popBackStack(ShowNoteFragment.BACK_STACK_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE)
//                }
//            }
        })
        viewModel.currentNote().observe(viewLifecycleOwner, noteObserver)
    }

    private fun openCalendarDialog() {
        fragmentManager?.run {
            val calendar = Calendar.getInstance().apply { timeInMillis = viewModel.currentNote().value?.date ?: System.currentTimeMillis() }
            val dialog = SelectDateFragment.initMe(calendar)
            dialog.listener = DatePickerDialog.OnDateSetListener { _, y, m, d -> viewModel.setDate(y, m, d) }
            dialog.show(this, TAG_CALENDAR_DIAlOG)
        }
    }

    companion object {
        private const val EXTRA_ID = "EXTRA_ID"
        private const val EXTRA_SCROLL = "EXTRA_SCROLL"
        private const val EXTRA_BACK_BUTTON_VISIBLE = "EXTRA_BACK_BUTTON_VISIBLE"

        private const val TAG_CALENDAR_DIAlOG = "TAG_CALENDAR_DIAlOG"

        fun initMe(id: Long? = null, forceBackButtonVisible: Boolean = false, scrollY: Int? = null): EditNoteFragment {
            val fragment = EditNoteFragment()
            val args = Bundle()
            id?.run { args.putLong(EXTRA_ID, this) }
            args.putBoolean(EXTRA_BACK_BUTTON_VISIBLE, forceBackButtonVisible)
            scrollY?.run { if (this != 0) args.putInt(EXTRA_SCROLL, this) }
            fragment.arguments = args
            return fragment
        }
    }
}