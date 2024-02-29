package com.lebartodev.lnote.edit

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lebartodev.core.base.BaseFragment
import com.lebartodev.core.base.fragmentNullableArgs
import com.lebartodev.core.data.NoteData
import com.lebartodev.core.di.utils.CoreComponentProvider
import com.lebartodev.core.di.utils.ViewModelFactory
import com.lebartodev.core.utils.viewBinding
import com.lebartodev.lnote.edit.databinding.FragmentEditNoteBinding
import com.lebartodev.lnote.edit.di.DaggerEditNoteComponent
import com.lebartodev.lnote.edit.utils.EditUtils
import com.lebartodev.lnote.feature_attach.ui.AttachPanelFragment
import com.lebartodev.lnote.utils.NotePhotosAdapter
import com.lebartodev.lnote.utils.extensions.animateSlideBottomVisibility
import com.lebartodev.lnote.utils.extensions.animateSlideTopVisibility
import com.lebartodev.lnote.utils.extensions.formattedHint
import com.lebartodev.lnote.utils.extensions.onLayout
import com.lebartodev.lnote.utils.ui.PaddingDecoration
import com.lebartodev.lnote.utils.ui.SelectDateFragment
import com.lebartodev.lnote.utils.ui.TextWatcherAdapter
import com.lebartodev.lnote.utils.ui.toPx
import java.util.Calendar
import javax.inject.Inject

class EditNoteFragment : BaseFragment() {
    private val binding by viewBinding(FragmentEditNoteBinding::inflate)

    private val adapter = NotePhotosAdapter()
    private val noteId: Long? by fragmentNullableArgs()
    private val scrollArg: Int? by fragmentNullableArgs()
    private var scroll: Int? = scrollArg
    private val noteObserver: Observer<NoteData> = Observer { noteData ->
        val description = noteData.text ?: ""
        val title = noteData.title
        val time = noteData.date

        with(binding.textTitle) {
            removeTextChangedListener(titleTextWatcher)
            if (description != hint) {
                hint = if (description.isNotEmpty()) {
                    description.formattedHint()
                } else {
                    context?.getString(R.string.title_hint)
                }
            }
            if (text.toString() != title) {
                setText(title)
            }
            addTextChangedListener(titleTextWatcher)
        }

        with(binding.textDescription) {
            removeTextChangedListener(descriptionTextWatcher)
            if (text.toString() != description) {
                setText(description)
            }
            addTextChangedListener(descriptionTextWatcher)
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
        with(binding.textTitleActionBar) {
            hint = binding.textTitle.hint
            text = binding.textTitle.text
        }
        adapter.updateData(noteData.photos.map { it.path })
        binding.photosList.visibility = if (noteData.photos.isEmpty()) View.GONE else View.VISIBLE
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: NoteEditViewModel by viewModels { viewModelFactory }
    private val descriptionTextWatcher = object : TextWatcherAdapter() {
        override fun afterTextChanged(e: Editable?) {
            viewModel.setDescription(e?.toString() ?: "")
        }
    }
    private val titleTextWatcher = object : TextWatcherAdapter() {
        override fun afterTextChanged(e: Editable?) {
            viewModel.setTitle(e?.toString() ?: "")
            binding.textTitleActionBar.text = e?.toString() ?: ""
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postponeEnterTransition()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerEditNoteComponent.builder()
            .coreComponent((context.applicationContext as CoreComponentProvider).coreComponent)
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
        super.onViewCreated(view, savedInstanceState)

        binding.dateChip.transitionName = resources.getString(
            R.string.note_date_transition_name,
            noteId?.toString() ?: "local"
        )

        val visibleTitleLimit = 56f.toPx(resources)
        binding.noteContent.setOnScrollChangeListener { _: NestedScrollView?, _: Int, scrollY: Int, _: Int, oldScrollY: Int ->
            with(binding.textTitleActionBar) {
                if (scrollY >= visibleTitleLimit && oldScrollY < visibleTitleLimit) {
                    animate().cancel()
                    animate().alpha(1f).start()
                } else if (scrollY < visibleTitleLimit && oldScrollY > visibleTitleLimit) {
                    animate().cancel()
                    animate().alpha(0f).start()
                }
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
            context, 1, RecyclerView.HORIZONTAL,
            false
        )
        binding.photosList.adapter = adapter
        binding.photosList.addItemDecoration(PaddingDecoration(8f.toPx(resources)))

        binding.attachButton.setOnClickListener {
            AttachPanelFragment().show(childFragmentManager, AttachPanelFragment.TAG)
        }

        setupEditViewModel()
        if (savedInstanceState == null)
            viewModel.loadNote(noteId)


        parentFragmentManager.findFragmentByTag(TAG_CALENDAR_DIAlOG)?.run {
            (this as SelectDateFragment).listener =
                DatePickerDialog.OnDateSetListener { _, y, m, d ->
                    viewModel.setDate(y, m, d)
                }
        }
        childFragmentManager.setFragmentResultListener(
            AttachPanelFragment.ATTACH_REQUEST_KEY, viewLifecycleOwner
        ) { _, bundle ->
            viewModel.addPhoto(bundle.getString(AttachPanelFragment.PHOTO_PATH) ?: "")
        }
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
        viewModel.saveResult().observe(viewLifecycleOwner) {
            if (it == true) {
                setFragmentResult(EditUtils.SAVE_NOTE_REQUEST_KEY, Bundle())
                binding.textTitle.clearFocus()
                binding.textDescription.clearFocus()
                sharedElementReturnTransition = null
                parentFragmentManager.popBackStack()
            }
        }
        viewModel.deleteResult().observe(viewLifecycleOwner) {
            if (it == true) {
                setFragmentResult(EditUtils.DELETE_NOTE_REQUEST_KEY, Bundle())
                binding.textTitle.clearFocus()
                binding.textDescription.clearFocus()
                sharedElementReturnTransition = null
                parentFragmentManager.popBackStack()
            }
        }
        viewModel.currentNote().observe(viewLifecycleOwner, noteObserver)
    }

    private fun openCalendarDialog() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = viewModel.currentNote().value?.date
            ?: System.currentTimeMillis()
        val dialog = SelectDateFragment.initMe(calendar)
        dialog.listener = DatePickerDialog.OnDateSetListener { _, y, m, d ->
            viewModel.setDate(y, m, d)
        }
        dialog.show(parentFragmentManager, TAG_CALENDAR_DIAlOG)
    }

    companion object {
        private const val EXTRA_ID = "noteId"
        private const val EXTRA_SCROLL = "scrollArg"

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