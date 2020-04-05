package com.lebartodev.lnote.common.edit

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.lebartodev.lnote.R
import com.lebartodev.lnote.base.BaseFragment
import com.lebartodev.lnote.common.LNoteApplication
import com.lebartodev.lnote.common.details.ShowNoteFragment
import com.lebartodev.lnote.data.NoteData
import com.lebartodev.lnote.data.entity.Status
import com.lebartodev.lnote.di.notes.DaggerNotesComponent
import com.lebartodev.lnote.utils.LNoteViewModelFactory
import com.lebartodev.lnote.utils.extensions.formattedHint
import com.lebartodev.lnote.utils.ui.SelectDateFragment
import com.lebartodev.lnote.utils.ui.toPx
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class EditNoteFragment : BaseFragment() {
    private lateinit var descriptionTextView: TextView
    private lateinit var titleTextView: TextView
    private lateinit var fullScreenButton: ImageButton
    private lateinit var deleteButton: ImageButton
    private lateinit var saveNoteButton: MaterialButton
    private lateinit var calendarButton: ImageButton
    private lateinit var dateChip: Chip
    private lateinit var backButton: ImageButton
    private lateinit var actionBarTitleTextView: TextView
    private lateinit var noteContent: NestedScrollView
    private var noteId: Long? = null
    private var scroll: Int? = null
    private val noteObserver: Observer<NoteData> = Observer { noteData ->
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
        if (time != null) {
            dateChip.visibility = View.VISIBLE
            dateChip.text = SimpleDateFormat(resources.getString(R.string.date_pattern), Locale.US).format(Date(time))
        } else {
            dateChip.visibility = View.GONE
        }
        if (noteData.id == noteId && noteId != null)
            startPostponedEnterTransition()
        if (scroll != null && scroll != 0 && !noteData.text.isNullOrEmpty()) {
            noteContent.post {
                noteContent.scrollTo(0, scroll ?: 0)
                startPostponedEnterTransition()
                scroll = null
            }
        }
        actionBarTitleTextView.hint = titleTextView.hint
        actionBarTitleTextView.text = titleTextView.text
    }

    @Inject
    lateinit var viewModelFactory: LNoteViewModelFactory
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
        context?.run {
            DaggerNotesComponent.builder()
                    .appComponent(LNoteApplication[this].appComponent)
                    .context(this)
                    .build()
                    .inject(this@EditNoteFragment)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_note, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        scroll = arguments?.getInt(EXTRA_SCROLL)
        noteId = arguments?.getLong(EXTRA_ID, -1)
        if (noteId == -1L) {
            noteId = null
        }
        if (noteId != null || scroll != null || scroll != 0) {
            postponeEnterTransition()
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
        arguments?.getString(EXTRA_TEXT)?.run { descriptionTextView.text = this }
        arguments?.getString(EXTRA_TITLE)?.run { titleTextView.text = this }

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

        deleteButton.setOnClickListener { if (noteId == null) viewModel.deleteCurrentNote() else viewModel.deleteEditedNote() }
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

    private fun setupEditViewModel() {
        viewModel = activity?.run { ViewModelProviders.of(this, viewModelFactory)[NoteEditViewModel::class.java] } ?: throw NullPointerException()
//        if (noteId != null)
//            viewModel.onCurrentNoteCleared()
        viewModel.pendingDelete().observe(viewLifecycleOwner, Observer {
            if (it == true) {
                titleTextView.clearFocus()
                descriptionTextView.clearFocus()
                if (noteId == null) {
                    fragmentManager?.popBackStack()
                } else {
                    fragmentManager?.popBackStack(ShowNoteFragment.BACK_STACK_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                }
            }
        })
        viewModel.saveResult().observe(viewLifecycleOwner, Observer { obj ->
            if (obj != null) {
                if (obj.status == Status.ERROR) {
                    viewModel.currentNote().observe(viewLifecycleOwner, noteObserver)
                    Toast.makeText(context, getString(R.string.error_note_create), Toast.LENGTH_SHORT).show()
                } else if (obj.status == Status.SUCCESS) {
                    fragmentManager?.popBackStack()
                }
            }
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

    override fun onDestroy() {
        super.onDestroy()
        if (noteId != null)
            viewModel.clearCurrentNote()
    }


    companion object {
        private const val EXTRA_ID = "EXTRA_ID"
        private const val EXTRA_SCROLL = "EXTRA_SCROLL"
        private const val EXTRA_TITLE = "EXTRA_TITLE"
        private const val EXTRA_TEXT = "EXTRA_TEXT"
        private const val EXTRA_BACK_BUTTON_VISIBLE = "EXTRA_BACK_BUTTON_VISIBLE"
        private const val TAG_CALENDAR_DIAlOG = "TAG_CALENDAR_DIAlOG"

        fun initMe(id: Long, title: String?, text: String?, scrollY: Int = 0): EditNoteFragment {
            val fragment = EditNoteFragment()
            val args = Bundle()
            args.putLong(EXTRA_ID, id)
            args.putString(EXTRA_TITLE, title)
            args.putString(EXTRA_TEXT, text)
            args.putInt(EXTRA_SCROLL, scrollY)
            fragment.arguments = args
            return fragment
        }

        fun initMe(forceBackButtonVisible: Boolean = false, scrollY: Int = 0): EditNoteFragment {
            val fragment = EditNoteFragment()
            val args = Bundle()
            args.putInt(EXTRA_SCROLL, scrollY)
            args.putBoolean(EXTRA_BACK_BUTTON_VISIBLE, forceBackButtonVisible)
            fragment.arguments = args
            return fragment
        }
    }
}