package com.lebartodev.lnote.common.details


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
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
import com.lebartodev.lnote.data.entity.Status
import com.lebartodev.lnote.repository.NoteContainer
import com.lebartodev.lnote.utils.LNoteViewModelFactory
import java.util.*

class NoteCreationView : ConstraintLayout {
    private var notesViewModel: NoteEditViewModel? = null

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

    private var fragment: Fragment? = null
    private var clickListener: ClickListener? = null
    var isMoreOpen = false

    private val descriptionTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            notesViewModel?.onDescriptionChanged(s?.toString())
            NoteContainer.currentNote.text = s?.toString()
        }
    }
    private val titleTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            NoteContainer.currentNote.title = s?.toString()
        }
    }
    private val listener = DatePickerDialog.OnDateSetListener { _, y, m, d ->
        notesViewModel?.setDate(y, m, d)
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        inflate(context, R.layout.view_note_creation, this)
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
            clickListener?.onSaveClicked()
            val savedTitle = if (titleText.text.isNullOrEmpty()) titleText.hint.toString() else titleText.text.toString()
            notesViewModel?.saveNote(title = savedTitle, text = descriptionText.text.toString())
            titleText.setText("")
            descriptionText.setText("")
            dateChip.text = ""
            dateChip.visibility = View.GONE
            titleText.clearFocus()
            descriptionText.clearFocus()
        }
        deleteButton.setOnClickListener {
            NoteContainer.tempNote.text = NoteContainer.currentNote.text
            NoteContainer.tempNote.title = NoteContainer.currentNote.title
            NoteContainer.tempNote.date = NoteContainer.currentNote.date
            dateChip.visibility = View.GONE
            dateChip.text = ""
            titleText.setText("")
            descriptionText.setText("")
            titleText.clearFocus()
            descriptionText.clearFocus()
            clickListener?.onDeleteClicked()
        }
        calendarButton.setOnClickListener {
            openCalendarDialog()
        }
        dateChip.setOnClickListener {
            openCalendarDialog()
        }
        descriptionText.addTextChangedListener(descriptionTextWatcher)
        titleText.addTextChangedListener(titleTextWatcher)

        setupSheetView()
    }

    fun setupFragment(fragment: Fragment, viewModelFactory: LNoteViewModelFactory, clickListener: ClickListener,
                      isMoreOpen: Boolean) {
        this.isMoreOpen = isMoreOpen
        this.clickListener = clickListener
        this.fragment = fragment
        this.notesViewModel = ViewModelProviders.of(fragment, viewModelFactory)[NoteEditViewModel::class.java]
        if (isMoreOpen) {
            deleteButton.visibility = View.VISIBLE
            calendarButton.visibility = View.VISIBLE
            fabMore.setImageResource(R.drawable.ic_arrow_right_24)
        }
        titleText.setText(NoteContainer.currentNote.title)
        descriptionText.setText(NoteContainer.currentNote.text)
        this.notesViewModel?.apply {
            descriptionTextLiveData.observe(fragment, Observer {
                if (it != null) {
                    if (it.isNotEmpty())
                        titleText.hint = it
                    else
                        titleText.hint = context.getString(R.string.title_hint)
                }
            })
            selectedDateString().observe(fragment, Observer {
                if (it.isNotEmpty()) {
                    dateChip.visibility = View.VISIBLE
                } else {
                    dateChip.visibility = View.VISIBLE
                }
                dateChip.text = it
            })
            getSaveResult().observe(fragment, Observer { obj ->
                if (obj.status == Status.ERROR) {
                    Toast.makeText(context, context.getString(R.string.error_note_create), Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupSheetView() {
        fullScreenButton.setOnClickListener {
            (fragment as BaseFragment).hideKeyboardListener(titleText) {
                val nextFragment = EditNoteFragment.startMe(titleText.text.toString(), titleText.hint.toString(),
                        descriptionText.text.toString(), dateChip.text.toString())
                clickListener?.onFullScreenClicked()

                val exitFade = Fade(Fade.OUT)
                exitFade.duration = resources.getInteger(R.integer.animation_duration).toLong() / 2
                fragment?.exitTransition = exitFade

                val returnFade = Fade(Fade.IN)
                returnFade.duration = resources.getInteger(R.integer.animation_duration).toLong()
                fragment?.returnTransition = returnFade


                val enterFade = Fade(Fade.IN)
                enterFade.startDelay = resources.getInteger(R.integer.animation_duration).toLong() / 2
                enterFade.duration = resources.getInteger(R.integer.animation_duration).toLong() / 2

                nextFragment.enterTransition = enterFade

                val enterTransitionSet = TransitionSet()
                enterTransitionSet.addTransition(
                        TransitionInflater.from(context).inflateTransition(android.R.transition.move))
                enterTransitionSet.startDelay = 0
                enterTransitionSet.duration = resources.getInteger(R.integer.animation_duration).toLong()

                nextFragment.sharedElementEnterTransition = enterTransitionSet

                val transaction = fragment?.fragmentManager
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

        fabMore.setOnClickListener {
            val constraintLayout = this
            TransitionManager.beginDelayedTransition(constraintLayout)
            isMoreOpen = !isMoreOpen
            val set = ConstraintSet()
            set.clone(constraintLayout)
            set.setVisibility(calendarButton.id, if (isMoreOpen) ConstraintSet.VISIBLE else ConstraintSet.GONE)
            set.setVisibility(deleteButton.id, if (isMoreOpen) ConstraintSet.VISIBLE else ConstraintSet.GONE)
            fabMore.setImageResource(if (isMoreOpen) R.drawable.ic_arrow_right_24 else R.drawable.ic_drop_down_24)
            set.applyTo(constraintLayout)
        }
    }

    private fun openCalendarDialog() {
        var selectedDate = notesViewModel?.selectedDate?.value
        if (selectedDate == null)
            selectedDate = Calendar.getInstance()
        selectedDate?.let {
            DatePickerDialog(context, listener,
                    it.get(Calendar.YEAR),
                    it.get(Calendar.MONTH),
                    it.get(Calendar.DAY_OF_MONTH))
                    .show()
        }
    }


    override fun onDetachedFromWindow() {
        descriptionText.removeTextChangedListener(descriptionTextWatcher)
        titleText.removeTextChangedListener(titleTextWatcher)
        super.onDetachedFromWindow()
    }

    fun setContent(title: String?, text: String?) {
        titleText.setText(title)
        descriptionText.setText(text)
    }

    interface ClickListener {
        fun onSaveClicked()
        fun onFullScreenClicked()
        fun onDeleteClicked()
    }
}
