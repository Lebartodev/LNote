package com.lebartodev.lnote.common.creation


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.transition.TransitionManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.lebartodev.lnote.R
import com.lebartodev.lnote.common.note_edit.NoteFragment
import com.lebartodev.lnote.data.entity.Status
import com.lebartodev.lnote.utils.LNoteViewModelFactory
import java.util.*

class NoteCreationView : ConstraintLayout {
    private val saveNoteButton: MaterialButton
    private lateinit var titleText: EditText
    private val descriptionText: EditText
    private val dateText: TextInputEditText
    private val dateLayout: TextInputLayout
    private val fabMore: FloatingActionButton
    private val fullScreenButton: ImageView
    private var notesViewModel: NoteEditViewModel? = null
    private var activity: FragmentActivity? = null
    private var saveClickListener: SaveClickListener? = null

    fun setupActivity(activity: FragmentActivity, viewModelFactory: LNoteViewModelFactory,
                      saveClickListener: SaveClickListener) {
        this.saveClickListener = saveClickListener
        this.activity = activity
        notesViewModel = ViewModelProviders.of(activity, viewModelFactory)[NoteEditViewModel::class.java]
        this.notesViewModel?.apply {
            this.descriptionTextLiveData.observe(activity, Observer {
                if (titleText.tag == TAG_TITLE_NOT_CHANGED && it != null) {
                    titleText.setText(it)
                }
            })
            selectedDateString().observe(activity, Observer {
                dateText.setText(it)
            })

            getSaveResult().observe(activity, Observer { obj ->
                if (obj.status == Status.ERROR) {
                    Toast.makeText(activity, activity.getString(R.string.error_note_create), Toast.LENGTH_SHORT).show()
                }
            })
        }


    }

    private val descriptionTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            notesViewModel?.onDescriptionChanged(s?.toString())
        }
    }
    private val titleTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (titleText.hasFocus() && s?.length ?: 0 > 0)
                disableAutoTitle()

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s.isNullOrEmpty()) {
                enableAutoTitle()
            }
        }
    }
    private val listener = DatePickerDialog.OnDateSetListener { _, y, m, d ->
        notesViewModel?.setDate(y, m, d)
    }

    init {
        inflate(context, R.layout.view_note_creation, this)
        saveNoteButton = findViewById(R.id.save_button)
        titleText = findViewById(R.id.text_title)
        descriptionText = findViewById(R.id.text_description)
        dateText = findViewById(R.id.date_text)
        dateLayout = findViewById(R.id.date_layout)
        fabMore = findViewById(R.id.fab_more)
        fullScreenButton = findViewById(R.id.full_screen_button)

        saveNoteButton.setOnClickListener {
            saveClickListener?.onSaveClicked()
            notesViewModel?.saveNote(title = titleText.text.toString(), text = descriptionText.text.toString())
            titleText.setText("")
            descriptionText.setText("")
            titleText.clearFocus()
            descriptionText.clearFocus()
            enableAutoTitle()
        }
        dateLayout.setOnClickListener {
            openCalendarDialog()
        }
        descriptionText.addTextChangedListener(descriptionTextWatcher)
        titleText.addTextChangedListener(titleTextWatcher)
        enableAutoTitle()

        setupBottomSheet()
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupBottomSheet() {
        val moreHiddenConstraintSet = ConstraintSet()
        val moreExpandedConstraintSet = ConstraintSet()
        var isMoreOpen = false

        fullScreenButton.setOnClickListener {
            activity?.let {
                it.supportFragmentManager.beginTransaction()
                        .replace(R.id.full_screen_note_layout,
                                NoteFragment.startMe(titleText.text.toString(), descriptionText.text.toString()))
                        .addToBackStack(null)
                        .commit()
            }
        }

        val constraintLayout = this
        moreHiddenConstraintSet.clone(constraintLayout)
        moreExpandedConstraintSet.clone(context, R.layout.view_note_creation_expanded)
        fabMore.setOnClickListener {
            TransitionManager.beginDelayedTransition(constraintLayout)
            val constraint = if (isMoreOpen) moreHiddenConstraintSet else moreExpandedConstraintSet
            isMoreOpen = !isMoreOpen
            fabMore.setImageResource(if (isMoreOpen) R.drawable.ic_arrow_up_24 else R.drawable.ic_drop_down_24)
            constraint.applyTo(constraintLayout)
        }

        dateText.setOnTouchListener { _, event ->
            val DRAWABLE_RIGHT = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (dateText.right - dateText.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
                    notesViewModel?.clearDate()
                } else {
                    openCalendarDialog()
                }
                return@setOnTouchListener true
            }
            false
        }
    }

    private fun enableAutoTitle() {
        titleText.tag = TAG_TITLE_NOT_CHANGED
        titleText.setTextColor(resources.getColor(R.color.black_40))
    }

    private fun disableAutoTitle() {
        titleText.tag = null
        titleText.setTextColor(resources.getColor(R.color.textColorPrimary))
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

    interface SaveClickListener {
        fun onSaveClicked()
    }

    companion object {
        private const val TAG_TITLE_NOT_CHANGED = "TAG_TITLE_NOT_CHANGED"
    }
}
