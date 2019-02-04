package com.lebartodev.lnote.common.notes

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.lebartodev.lnote.R
import com.lebartodev.lnote.base.BaseActivity
import com.lebartodev.lnote.data.entity.Note
import com.lebartodev.lnote.data.entity.Status
import com.lebartodev.lnote.di.component.AppComponent
import com.lebartodev.lnote.utils.*
import java.util.*
import javax.inject.Inject


class NotesActivity : BaseActivity(), NotesScreen.View {
    private val fabAdd by lazy { findViewById<FloatingActionButton>(R.id.fab_add) }
    private val bottomAppBar by lazy { findViewById<BottomAppBar>(R.id.bottom_app_bar) }
    private val saveNoteButton by lazy { findViewById<MaterialButton>(R.id.save_button) }
    private val titleText by lazy { findViewById<EditText>(R.id.text_title) }
    private val descriptionText by lazy { findViewById<EditText>(R.id.text_description) }
    private val notesList by lazy { findViewById<RecyclerView>(R.id.notes_list) }
    private val additionalGroup by lazy { findViewById<Group>(R.id.additional_group) }
    private val fabMore by lazy { findViewById<FloatingActionButton>(R.id.fab_more) }
    private val dateLayout by lazy { findViewById<TextInputLayout>(R.id.date_layout) }
    private val dateText by lazy { findViewById<TextInputEditText>(R.id.date_text) }
    private val adapter = NotesAdapter()
    private val bottomAddSheetBehavior by lazy {
        BottomSheetBehavior.from(findViewById<ConstraintLayout>(R.id.bottom_sheet_add))
    }
    @Inject
    lateinit var viewModelFactory: LNoteViewModelFactory
    private lateinit var notesViewModel: NotesViewModel

    private val listener = DatePickerDialog.OnDateSetListener { _, y, m, d ->
        notesViewModel.setDate(y, m, d)
    }

    private val descriptionTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            notesViewModel.onDescriptionChanged(s?.toString())
        }
    }
    private val titleTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (titleText.hasFocus() && s?.length ?: 0 > 0)
                titleText.tag = null

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s.isNullOrEmpty()) {
                titleText.tag = TAG_TITLE_NOT_CHANGED
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notesViewModel = ViewModelProviders.of(this, viewModelFactory)[NotesViewModel::class.java]
        notesViewModel.loadNotes().observe(this, Observer {
            if (it.error == null && it.data != null) {
                onNotesLoaded(it.data)
            } else {
                error("loadNotes", it.error)

            }
        })
        notesViewModel.selectedDateString().observe(this, Observer {
            dateText.setText(it)
        })
        setSupportActionBar(bottomAppBar)
        notesList.layoutManager = LinearLayoutManager(this)
        notesList.adapter = adapter
        notesList.addItemDecoration(NotesItemDecoration(8f.toPx(resources),
                8f.toPx(resources),
                16f.toPx(resources),
                16f.toPx(resources)))
        bottomAddSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomAddSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
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
                    hideKeyboard(titleText)
                    closeAdditionalGroup()
                }
            }
        })
        fabAdd.setOnClickListener {
            bottomAddSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        saveNoteButton.setOnClickListener {
            bottomAddSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            notesViewModel.saveNote(title = titleText.text.toString(), text = descriptionText.text.toString())
                    .observe(this, Observer { obj ->
                        if (obj.status == Status.ERROR) {
                            toast(getString(R.string.error_note_create))
                        }
                    })
            titleText.setText("")
            descriptionText.setText("")
            titleText.clearFocus()
            descriptionText.clearFocus()
            titleText.tag = TAG_TITLE_NOT_CHANGED
        }
        fabMore.setOnClickListener {
            if (additionalGroup.visibility == View.GONE) {
                openAdditionalGroup()
            } else {
                closeAdditionalGroup()
            }
        }
        dateLayout.setOnClickListener {
            openCalendarDialog()
        }
        dateText.setOnTouchListener { _, event ->
            val DRAWABLE_RIGHT = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (dateText.right - dateText.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
                    notesViewModel.clearDate()
                } else {
                    openCalendarDialog()
                }
                return@setOnTouchListener true
            }
            false
        }
        notesList.setOnTouchListener { _: View, motionEvent: MotionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN)
                bottomAddSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            false

        }
        descriptionText.addTextChangedListener(descriptionTextWatcher)
        titleText.addTextChangedListener(titleTextWatcher)
        titleText.tag = TAG_TITLE_NOT_CHANGED
        notesViewModel.descriptionTextLiveData.observe(this, Observer {
            if (titleText.tag == TAG_TITLE_NOT_CHANGED && it != null) {
                titleText.setText(it)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.settings_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.app_bar_settings -> {

            }
        }
        return true
    }

    private fun openCalendarDialog() {
        var selectedDate = notesViewModel.selectedDate.value
        if (selectedDate == null)
            selectedDate = Calendar.getInstance()
        selectedDate?.let {
            DatePickerDialog(this, listener,
                    it.get(Calendar.YEAR),
                    it.get(Calendar.MONTH),
                    it.get(Calendar.DAY_OF_MONTH))
                    .show()
        }
    }

    private fun openAdditionalGroup() {
        fabMore.setImageResource(R.drawable.ic_arrow_up_24)
        additionalGroup.visibility = View.VISIBLE
        bottomAddSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun closeAdditionalGroup() {
        fabMore.setImageResource(R.drawable.ic_drop_down_24)
        additionalGroup.visibility = View.GONE
    }

    override fun onNotesLoaded(notes: List<Note>) {
        adapter.data = notes
    }

    override fun onLoadError(throwable: Throwable) {
        toast(throwable.message)
    }

    public override fun setupComponent(component: AppComponent) {
        component.inject(this)
    }

    override fun onDestroy() {
        descriptionText.removeTextChangedListener(descriptionTextWatcher)
        titleText.removeTextChangedListener(titleTextWatcher)
        super.onDestroy()
    }

    companion object {
        private const val TAG_TITLE_NOT_CHANGED = "TAG_TITLE_NOT_CHANGED"
    }
}
