package com.lebartodev.lnote.edit.creation

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.text.Editable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.findFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.lebartodev.core.data.NoteData
import com.lebartodev.core.di.utils.CoreComponentProvider
import com.lebartodev.core.di.utils.ViewModelFactory
import com.lebartodev.lnote.edit.NoteEditViewModel
import com.lebartodev.lnote.edit.R
import com.lebartodev.lnote.edit.databinding.ViewNoteCreationBinding
import com.lebartodev.lnote.edit.di.DaggerEditNoteComponent
import com.lebartodev.lnote.feature_attach.ui.AttachPanelFragment
import com.lebartodev.lnote.utils.NotePhotosAdapter
import com.lebartodev.lnote.utils.extensions.formattedHint
import com.lebartodev.lnote.utils.ui.NoteTransitionDrawable
import com.lebartodev.lnote.utils.ui.PaddingDecoration
import com.lebartodev.lnote.utils.ui.SelectDateFragment
import com.lebartodev.lnote.utils.ui.TextWatcherAdapter
import com.lebartodev.lnote.utils.ui.toPx
import java.util.*
import javax.inject.Inject

@SuppressLint("ClickableViewAccessibility")
class NoteCreationView : ConstraintLayout {
    private val adapter = NotePhotosAdapter()
    private var binding = ViewNoteCreationBinding.inflate(LayoutInflater.from(context), this)
    private val noteObserver = Observer<NoteData> {
        updateNoteData(it)
    }
    var closeListener: (() -> Unit)? = null

    private var isMoreOpen = false

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: NoteEditViewModel by lazy {
        ViewModelProvider(findFragment<Fragment>(), viewModelFactory)[NoteEditViewModel::class.java]
    }

    private val descriptionTextWatcher = object : TextWatcherAdapter() {
        override fun afterTextChanged(e: Editable?) {
            viewModel.setDescription(e?.toString() ?: "")
        }
    }
    private val titleTextWatcher = object : TextWatcherAdapter() {
        override fun afterTextChanged(e: Editable?) {
            viewModel.setTitle(e?.toString() ?: "")
        }
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs,
        defStyleAttr
    )


    init {
        isSaveEnabled = true

        background = NoteTransitionDrawable(ContextCompat.getColor(context, R.color.white), 0f)

        binding.noteContent.transitionName = resources.getString(
            R.string.note_content_transition_name, "local"
        )
        binding.textTitle.transitionName = resources.getString(
            R.string.note_title_transition_name,
            "local"
        )
        binding.textDescription.transitionName = resources.getString(
            R.string.note_description_transition_name, "local"
        )
        binding.dateChip.transitionName = resources.getString(
            R.string.note_date_transition_name,
            "local"
        )

        binding.saveButton.setOnClickListener {
            viewModel.saveNote()
            closeListener?.invoke()
        }
        binding.deleteButton.setOnClickListener {
            viewModel.deleteEditedNote()
            closeListener?.invoke()
        }
        binding.attachButton.setOnClickListener {
            AttachPanelFragment()
                .show(findFragment<Fragment>().childFragmentManager, AttachPanelFragment.TAG)
        }

        binding.dateChip.setOnCloseIconClickListener { viewModel.clearDate() }
        binding.calendarButton.setOnClickListener { openCalendarDialog(null) }
        binding.textDescription.addTextChangedListener(descriptionTextWatcher)
        binding.textTitle.addTextChangedListener(titleTextWatcher)
        binding.fabMore.setOnClickListener { setMoreOpen(!isMoreOpen) }
        with(binding.photosList) {
            adapter = adapter
            addItemDecoration(PaddingDecoration(8f.toPx(resources)))
            layoutManager = GridLayoutManager(context, 1, RecyclerView.HORIZONTAL, false)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        DaggerEditNoteComponent.builder()
            .coreComponent((context.applicationContext as CoreComponentProvider).coreComponent)
            .build()
            .inject(this)
        viewModel.loadNote(null)
        viewModel.currentNote().observeForever(noteObserver)

        findFragment<Fragment>().childFragmentManager
            .setFragmentResultListener(
                AttachPanelFragment.ATTACH_REQUEST_KEY,
                findFragment<Fragment>().viewLifecycleOwner
            ) { _, bundle ->
                viewModel.addPhoto(bundle.getString(AttachPanelFragment.PHOTO_PATH) ?: "")
            }
    }

    override fun onSaveInstanceState(): Parcelable {
        val savedState = SavedState(super.onSaveInstanceState())
        savedState.isMoreOpen = isMoreOpen
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is SavedState) {
            super.onRestoreInstanceState(state.superState)
            isMoreOpen = state.isMoreOpen
            setMoreOpen(isMoreOpen, false)
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    private fun openCalendarDialog(selectedDateInMillis: Long?) {
        val selectedDate = Calendar.getInstance()
        selectedDateInMillis?.let {
            selectedDate.timeInMillis = it
        }
        selectedDate.let {
            (context as FragmentActivity).supportFragmentManager.run {
                val dialog = SelectDateFragment.initMe(it)
                dialog.listener = DatePickerDialog.OnDateSetListener { _, y, m, d ->
                    viewModel.setDate(y, m, d)
                }
                dialog.show(this, TAG_CALENDAR_DIAlOG)
            }
        }
    }

    override fun onDetachedFromWindow() {
        binding.textDescription.removeTextChangedListener(descriptionTextWatcher)
        binding.textTitle.removeTextChangedListener(titleTextWatcher)
        viewModel.currentNote().removeObserver(noteObserver)
        super.onDetachedFromWindow()
    }

    private fun updateNoteData(noteData: NoteData) {
        val description = noteData.text ?: ""
        val title = noteData.title
        val time = noteData.date
        val photos = noteData.photos
        with(binding.textTitle) {
            if (description != this.hint) {
                if (description.isNotEmpty()) {
                    this.hint = description.formattedHint()
                } else {
                    this.hint = context?.getString(R.string.title_hint)
                }
            }
            if (this.text.toString() != title) {
                this.setText(title)
            }

        }
        with(binding.textDescription) {
            if (text.toString() != description) {
                setText(description)
            }
        }
        with(binding.dateChip) {
            setDate(time)
            setOnClickListener { openCalendarDialog(time) }
        }
        binding.calendarButton.setOnClickListener {
            openCalendarDialog(time)
        }
        binding.photosList.visibility = if (photos.isEmpty()) View.GONE else View.VISIBLE
        adapter.updateData(photos.map { it.path })
    }

    private fun setMoreOpen(isMoreOpen: Boolean, animate: Boolean = true) {
        if (animate) {
            val constraintLayout = this
            TransitionManager.beginDelayedTransition(constraintLayout)
            val set = ConstraintSet()
            set.clone(constraintLayout)
            set.setVisibility(
                binding.calendarButton.id,
                if (isMoreOpen) ConstraintSet.VISIBLE else ConstraintSet.GONE
            )
            set.setVisibility(
                binding.attachButton.id,
                if (isMoreOpen) ConstraintSet.VISIBLE else ConstraintSet.GONE
            )
            set.setVisibility(
                binding.deleteButton.id,
                if (isMoreOpen) ConstraintSet.VISIBLE else ConstraintSet.GONE
            )
            binding.fabMore.setImageResource(
                if (isMoreOpen) R.drawable.ic_arrow_right_24 else R.drawable.ic_drop_down_24
            )
            set.applyTo(constraintLayout)
        } else {
            binding.deleteButton.visibility = if (isMoreOpen) View.VISIBLE else View.GONE
            binding.attachButton.visibility = if (isMoreOpen) View.VISIBLE else View.GONE
            binding.calendarButton.visibility = if (isMoreOpen) View.VISIBLE else View.GONE
            binding.fabMore.setImageResource(
                if (isMoreOpen) R.drawable.ic_arrow_right_24 else R.drawable.ic_drop_down_24
            )
        }
        this.isMoreOpen = isMoreOpen
    }

    private class SavedState : BaseSavedState {
        var isMoreOpen: Boolean = false

        constructor(parcel: Parcel) : super(parcel) {
            isMoreOpen = parcel.readByte().toInt() != 0
        }

        constructor(superState: Parcelable?) : super(superState)

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            super.writeToParcel(parcel, flags)
            parcel.writeByte((if (isMoreOpen) 1 else 0).toByte())
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel): SavedState {
                return SavedState(parcel)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }
    }

    companion object {
        private const val TAG_CALENDAR_DIAlOG = "TAG_CALENDAR_DIAlOG"
    }
}
