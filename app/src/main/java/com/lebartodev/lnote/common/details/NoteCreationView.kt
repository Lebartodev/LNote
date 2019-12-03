package com.lebartodev.lnote.common.details


import android.app.DatePickerDialog
import android.content.Context
import android.content.ContextWrapper
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.transition.Fade
import androidx.transition.TransitionInflater
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.lebartodev.lnote.R
import com.lebartodev.lnote.base.BaseFragment
import com.lebartodev.lnote.common.LNoteApplication
import com.lebartodev.lnote.common.notes.NotesFragment
import com.lebartodev.lnote.data.entity.Status
import com.lebartodev.lnote.di.notes.NotesModule
import com.lebartodev.lnote.utils.LNoteViewModelFactory
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class NoteCreationView : ConstraintLayout {
    @Inject
    lateinit var viewModelFactory: LNoteViewModelFactory
    private var viewModel: NoteEditViewModel? = null

    private val saveNoteButton: MaterialButton
    private val titleText: EditText
    private val descriptionText: EditText
    private val fabMore: FloatingActionButton
    private val background: View
    private val fullScreenButton: ImageButton
    private val calendarButton: ImageButton
    private val deleteButton: ImageButton
    private val divider: View
    private val dateChip: Chip

    private val fullScreenObserver: Observer<Boolean>

    private val descriptionTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            viewModel?.setDescription(s?.toString() ?: "")
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }
    }
    private val titleTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            viewModel?.setTitle(s?.toString() ?: "")
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }
    }
    private val listener = DatePickerDialog.OnDateSetListener { _, y, m, d ->
        viewModel?.setDate(y, m, d)
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs,
            defStyleAttr)

    init {
        inflate(context, R.layout.view_note_creation, this)
        context?.let { LNoteApplication[it].component().plus(NotesModule()).inject(this) }
        calendarButton = findViewById(R.id.calendar_button)
        deleteButton = findViewById(R.id.delete_button)
        background = findViewById(R.id.note_creation_background)
        saveNoteButton = findViewById(R.id.save_button)
        titleText = findViewById(R.id.text_title)
        descriptionText = findViewById(R.id.text_description)
        fabMore = findViewById(R.id.fab_more)
        fullScreenButton = findViewById(R.id.full_screen_button)
        divider = findViewById(R.id.add_divider)
        dateChip = findViewById(R.id.date_chip)

        saveNoteButton.setOnClickListener {
            viewModel?.saveNote()
            titleText.setText("")
            descriptionText.setText("")
            dateChip.text = ""
            dateChip.visibility = View.GONE
            titleText.clearFocus()
            descriptionText.clearFocus()
        }
        deleteButton.setOnClickListener {
            viewModel?.clearCurrentNote()
            dateChip.visibility = View.GONE
            dateChip.text = ""
            titleText.setText("")
            descriptionText.setText("")
            titleText.clearFocus()
            descriptionText.clearFocus()
        }
        dateChip.setOnCloseIconClickListener {
            viewModel?.clearDate()
        }
        calendarButton.setOnClickListener {
            openCalendarDialog(null)
        }
        descriptionText.addTextChangedListener(descriptionTextWatcher)
        titleText.addTextChangedListener(titleTextWatcher)

        fullScreenButton.setOnClickListener { viewModel?.toggleFullScreen() }
        fabMore.setOnClickListener { viewModel?.toggleMore() }

        viewModel = getActivity()?.run { ViewModelProviders.of(this, viewModelFactory)[NoteEditViewModel::class.java] } ?: throw NullPointerException()
        getActivity()?.run {
            val fragment = this.supportFragmentManager.findFragmentByTag(NotesFragment.TAG)
            fragment?.run {
                viewModel?.currentNote()?.observe(this, Observer { noteData ->
                    val description = noteData.text ?: ""
                    val title = noteData.title
                    val time = noteData.date

                    if (description != titleText.hint) {
                        if (description.isNotEmpty()) {
                            titleText.hint = viewModel?.getFormattedHint(description)
                        } else {
                            titleText.hint = context?.getString(R.string.title_hint)
                        }
                    }
                    if (titleText.text.toString() != title) {
                        titleText.setText(title)
                    }
                    if (descriptionText.text.toString() != description) {
                        descriptionText.setText(description)
                    }

                    if (time != null) {
                        dateChip.visibility = View.VISIBLE
                        dateChip.text = SimpleDateFormat(resources.getString(R.string.date_pattern),
                                Locale.US).format(Date(time))
                    } else {
                        dateChip.visibility = View.GONE
                    }
                    calendarButton.setOnClickListener {
                        openCalendarDialog(time)
                    }
                    dateChip.setOnClickListener {
                        openCalendarDialog(time)
                    }
                })
            }
            viewModel?.saveResult()?.observe(this, Observer { obj ->
                if (obj.status == Status.ERROR) {
                    Toast.makeText(context, context.getString(R.string.error_note_create),
                            Toast.LENGTH_SHORT).show()
                }
            })
            viewModel?.isMoreOpen()?.observe(this, Observer {
                val constraintLayout = this@NoteCreationView
                TransitionManager.beginDelayedTransition(constraintLayout)
                val set = ConstraintSet()
                set.clone(constraintLayout)
                set.setVisibility(calendarButton.id, if (it) ConstraintSet.VISIBLE else ConstraintSet.GONE)
                set.setVisibility(deleteButton.id, if (it) ConstraintSet.VISIBLE else ConstraintSet.GONE)
                fabMore.setImageResource(if (it) R.drawable.ic_arrow_right_24 else R.drawable.ic_drop_down_24)
                set.applyTo(constraintLayout)
            })

        }
        if (viewModel?.isMoreOpen()?.value == true) {
            deleteButton.visibility = View.VISIBLE
            calendarButton.visibility = View.VISIBLE
            fabMore.setImageResource(R.drawable.ic_arrow_right_24)
        }

        fullScreenObserver = Observer {
            if (it == true) {
                getActivity()?.supportFragmentManager?.findFragmentByTag(NotesFragment.TAG)?.run {
                    (this as BaseFragment).hideKeyboardListener(titleText) {
                        val nextFragment = EditNoteFragment
                                .initMe(titleText.text.toString(),
                                        titleText.hint.toString(),
                                        descriptionText.text.toString(),
                                        viewModel?.selectedDate()?.value)
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
                                ?.addSharedElement(titleText, titleText.transitionName)
                                ?.addSharedElement(background, background.transitionName)
                                ?.addSharedElement(saveNoteButton, saveNoteButton.transitionName)
                                ?.addSharedElement(descriptionText, descriptionText.transitionName)
                                ?.addSharedElement(fullScreenButton, fullScreenButton.transitionName)
                                ?.addSharedElement(dateChip, dateChip.transitionName)
                                ?.addSharedElement(divider, divider.transitionName)
                                ?.addToBackStack(null)
                        if (deleteButton.visibility == View.VISIBLE && calendarButton.visibility == View.VISIBLE) {
                            transaction?.addSharedElement(deleteButton, deleteButton.transitionName)
                                    ?.addSharedElement(calendarButton, calendarButton.transitionName)
                        }
                        transaction?.commit()
                    }
                }

            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        getActivity()?.supportFragmentManager?.findFragmentByTag(NotesFragment.TAG)?.run { viewModel?.fullScreenOpen()?.observe(this, fullScreenObserver) }

    }

    private fun openCalendarDialog(selectedDateInMillis: Long?) {
        val selectedDate = Calendar.getInstance()
        selectedDateInMillis?.let {
            selectedDate.timeInMillis = it
        }
        selectedDate?.let {
            DatePickerDialog(context, listener,
                    it.get(Calendar.YEAR),
                    it.get(Calendar.MONTH),
                    it.get(Calendar.DAY_OF_MONTH))
                    .show()
        }
    }

    private fun getActivity(): FragmentActivity? {
        var context = context
        while (context is ContextWrapper) {
            if (context is FragmentActivity) {
                return context
            }
            context = context.baseContext;
        }
        return null
    }


    override fun onDetachedFromWindow() {
        descriptionText.removeTextChangedListener(descriptionTextWatcher)
        titleText.removeTextChangedListener(titleTextWatcher)
        viewModel?.fullScreenOpen()?.removeObserver(fullScreenObserver)
        super.onDetachedFromWindow()
    }
}
