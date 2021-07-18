package com.lebartodev.lnote.edit

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lebartodev.core.base.BaseFragment
import com.lebartodev.core.data.NoteData
import com.lebartodev.core.di.utils.AppComponentProvider
import com.lebartodev.core.utils.viewBinding
import com.lebartodev.lnote.edit.databinding.FragmentEditNoteBinding
import com.lebartodev.lnote.edit.di.DaggerEditNoteComponent
import com.lebartodev.lnote.edit.utils.EditUtils
import com.lebartodev.lnote.feature_attach.AttachPanelFragment
import com.lebartodev.lnote.utils.NotePhotosAdapter
import com.lebartodev.lnote.utils.extensions.animateSlideBottomVisibility
import com.lebartodev.lnote.utils.extensions.animateSlideTopVisibility
import com.lebartodev.lnote.utils.extensions.formattedHint
import com.lebartodev.lnote.utils.extensions.onLayout
import com.lebartodev.lnote.utils.ui.PaddingDecoration
import com.lebartodev.lnote.utils.ui.SelectDateFragment
import com.lebartodev.lnote.utils.ui.toPx
import java.util.*
import javax.inject.Inject

class EditNoteFragment : BaseFragment() {
    private val binding by viewBinding(FragmentEditNoteBinding::inflate)

    private val adapter = NotePhotosAdapter()
    private var noteId: Long? = null
    private var scroll: Int? = null
    private val noteObserver: Observer<NoteData> = Observer { noteData ->
        binding.textTitle.removeTextChangedListener(titleTextWatcher)
        binding.textDescription.removeTextChangedListener(descriptionTextWatcher)

        val description = noteData.text ?: ""
        val title = noteData.title
        val time = noteData.date

        if (description != binding.textTitle.hint) {
            if (description.isNotEmpty()) {
                binding.textTitle.hint = description.formattedHint()
            } else {
                binding.textTitle.hint = context?.getString(R.string.title_hint)
            }
        }

        if (binding.textTitle.text.toString() != title) {
            binding.textTitle.setText(title)
        }
        if (binding.textDescription.text.toString() != description) {
            binding.textDescription.setText(description)
        }
        if (isSharedAnimationEnd)
            binding.dateChip.setDateAnimated(time)
        else
            binding.dateChip.setDate(time)
        if (scroll != null && !noteData.text.isNullOrEmpty()) {
            binding.noteContent.post {
                binding.noteContent.scrollTo(0, scroll ?: 0)
                startPostponedEnterTransition()
                scroll = null
            }
        } else if (noteId != null && noteData.id == noteId) {
            startPostponedEnterTransition()
        } else if (noteId == null) {
            startPostponedEnterTransition()
        }
        binding.textTitleActionBar.hint = binding.textTitle.hint
        binding.textTitleActionBar.text = binding.textTitle.text
        binding.textTitle.addTextChangedListener(titleTextWatcher)
        binding.textDescription.addTextChangedListener(descriptionTextWatcher)
        adapter.updateData(noteData.photos.map { it.path })
        binding.photosList.visibility = if (noteData.photos.isEmpty()) View.GONE else View.VISIBLE
    }

    @Inject
    lateinit var viewModelFactory: EditNoteViewModelFactory
    private val viewModel: NoteEditViewModel by lazy {
        ViewModelProvider(parentFragment ?: this, viewModelFactory)[NoteEditViewModel::class.java]
    }
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
            binding.textTitleActionBar.text = s?.toString() ?: ""
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postponeEnterTransition()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerEditNoteComponent.builder()
                .appComponent(
                        (context.applicationContext as AppComponentProvider).provideAppComponent())
                .context(context)
                .build()
                .inject(this)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.run {
            if (containsKey(EXTRA_SCROLL))
                scroll = getInt(EXTRA_SCROLL)
            if (containsKey(EXTRA_ID))
                noteId = getLong(EXTRA_ID)
        }
        super.onViewCreated(view, savedInstanceState)

        binding.dateChip.transitionName = resources.getString(R.string.note_date_transition_name,
                noteId?.toString() ?: "local")

        val visibleTitleLimit = 56f.toPx(resources)
        binding.noteContent.setOnScrollChangeListener { _: NestedScrollView?, _: Int, scrollY: Int, _: Int, oldScrollY: Int ->
            if (scrollY >= visibleTitleLimit && oldScrollY < visibleTitleLimit) {
                binding.textTitleActionBar.animate().cancel()
                binding.textTitleActionBar.animate().alpha(1f).start()
            } else if (scrollY < visibleTitleLimit && oldScrollY > visibleTitleLimit) {
                binding.textTitleActionBar.animate().cancel()
                binding.textTitleActionBar.animate().alpha(0f).start()
            }
        }

        binding.textDescription.addTextChangedListener(descriptionTextWatcher)
        binding.textTitle.addTextChangedListener(titleTextWatcher)
        binding.backButton.setOnClickListener {
            hideKeyboard()
            parentFragmentManager.popBackStack()
        }

        binding.saveButton.setOnClickListener {
            hideKeyboard()
            viewModel.currentNote().removeObserver(noteObserver)
            viewModel.saveNote()
        }

        binding.calendarButton.setOnClickListener { openCalendarDialog() }
        binding.dateChip.setOnClickListener { openCalendarDialog() }
        binding.dateChip.setOnCloseIconClickListener { viewModel.clearDate() }
        binding.photosList.layoutManager = GridLayoutManager(
                context, 1,
                RecyclerView.HORIZONTAL, false)
        binding.photosList.adapter = adapter
        binding.photosList.addItemDecoration(PaddingDecoration(
                8f.toPx(resources),
                8f.toPx(resources),
                8f.toPx(resources),
                8f.toPx(resources)
        ))

        binding.attachButton.setOnClickListener {
            AttachPanelFragment().show(childFragmentManager, AttachPanelFragment.TAG)
        }

        setupEditViewModel()
        if (savedInstanceState == null)
            noteId?.run { viewModel.loadNote(this) }

        parentFragmentManager.findFragmentByTag(TAG_CALENDAR_DIAlOG)?.run {
            (this as SelectDateFragment).listener = DatePickerDialog.OnDateSetListener { _, y, m, d ->
                viewModel.setDate(y, m, d)
            }
        }
        childFragmentManager
                .setFragmentResultListener(AttachPanelFragment.ATTACH_REQUEST_KEY,
                        viewLifecycleOwner,
                        { _, bundle ->
                            viewModel.addPhoto(
                                    bundle.getString(AttachPanelFragment.PHOTO_PATH) ?: "")
                        })
    }

    override fun onStartSharedAnimation(sharedElementNames: MutableList<String>) {
        listOf(binding.saveButton, binding.calendarButton)
                .filter { !sharedElementNames.contains(it.transitionName) }
                .forEach {
                    when (it.transitionName) {
                        binding.saveButton.transitionName -> {
                            it.onLayout {
                                it.visibility = View.GONE
                                it.animateSlideBottomVisibility(true)
                            }
                        }
                        binding.calendarButton.transitionName -> {
                            it.onLayout {
                                it.visibility = View.GONE
                                it.animateSlideTopVisibility(true)
                            }
                        }
                    }
                }
    }

    private fun setupEditViewModel() {
        viewModel.saveResult().observe(viewLifecycleOwner, {
            if (it == true) {
                setFragmentResult(EditUtils.SAVE_NOTE_REQUEST_KEY, Bundle())
                binding.textTitle.clearFocus()
                binding.textDescription.clearFocus()
                sharedElementReturnTransition = null
                parentFragmentManager.popBackStack()
            }
        })
        viewModel.deleteResult().observe(viewLifecycleOwner, {
            if (it == true) {
                setFragmentResult(EditUtils.DELETE_NOTE_REQUEST_KEY, Bundle())
                binding.textTitle.clearFocus()
                binding.textDescription.clearFocus()
                sharedElementReturnTransition = null
                parentFragmentManager.popBackStack()
            }
        })
        viewModel.currentNote().observe(viewLifecycleOwner, noteObserver)
    }

    private fun openCalendarDialog() {
        parentFragmentManager.run {
            val calendar = Calendar.getInstance()
                    .apply {
                        timeInMillis = viewModel.currentNote().value?.date
                                ?: System.currentTimeMillis()
                    }
            val dialog = SelectDateFragment.initMe(calendar)
            dialog.listener = DatePickerDialog.OnDateSetListener { _, y, m, d ->
                viewModel.setDate(y, m, d)
            }
            dialog.show(this, TAG_CALENDAR_DIAlOG)
        }
    }

    companion object {
        private const val EXTRA_ID = "EXTRA_ID"
        private const val EXTRA_SCROLL = "EXTRA_SCROLL"

        private const val TAG_CALENDAR_DIAlOG = "TAG_CALENDAR_DIAlOG"

        fun initMe(id: Long? = null, scrollY: Int? = null): EditNoteFragment {
            val fragment = EditNoteFragment()
            val args = Bundle()
            id?.run { args.putLong(EXTRA_ID, this) }
            scrollY?.run { if (this != 0) args.putInt(EXTRA_SCROLL, this) }
            fragment.arguments = args
            return fragment
        }
    }
}