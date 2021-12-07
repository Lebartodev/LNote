package com.lebartodev.lnote.show

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import androidx.transition.TransitionSet
import com.lebartodev.core.base.BaseFragment
import com.lebartodev.core.db.entity.Note
import com.lebartodev.core.di.utils.CoreComponentProvider
import com.lebartodev.core.utils.viewBinding
import com.lebartodev.lnote.edit.EditNoteFragment
import com.lebartodev.lnote.edit.utils.EditUtils
import com.lebartodev.lnote.show.databinding.FragmentShowNoteBinding
import com.lebartodev.lnote.show.di.DaggerShowNoteComponent
import com.lebartodev.lnote.utils.NotePhotosAdapter
import com.lebartodev.lnote.utils.extensions.animateSlideTopVisibility
import com.lebartodev.lnote.utils.extensions.onLayout
import com.lebartodev.lnote.utils.ui.PaddingDecoration
import com.lebartodev.lnote.utils.ui.toPx
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ShowNoteFragment : BaseFragment() {
    private lateinit var formatter: SimpleDateFormat
    private val binding by viewBinding(FragmentShowNoteBinding::inflate)
    private val adapter = NotePhotosAdapter()

    @Inject
    lateinit var viewModelFactory: ShowNoteViewModelFactory

    private val viewModel: ShowNoteViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[ShowNoteViewModel::class.java]
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerShowNoteComponent.builder()
                .context(context)
                .appComponent((context.applicationContext as CoreComponentProvider).coreComponent)
                .build()
                .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postponeEnterTransition()
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        formatter = SimpleDateFormat(resources.getString(R.string.date_pattern), Locale.US)
        val id = arguments?.getLong(EXTRA_ID)

        binding.textDescription.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP && !binding.textDescription.hasSelection()) {
                binding.editButton.callOnClick()
                true
            } else {
                false
            }
        }
        binding.textTitle.setOnClickListener {
            binding.editButton.callOnClick()
        }

        view.transitionName = resources.getString(R.string.note_container_transition_name,
                id?.toString() ?: "local")
        binding.noteContent.transitionName = resources.getString(
                R.string.note_content_transition_name, id?.toString() ?: "local")
        binding.textTitle.transitionName = resources.getString(R.string.note_title_transition_name,
                id?.toString() ?: "local")
        binding.textDescription.transitionName = resources.getString(
                R.string.note_description_transition_name, id?.toString() ?: "local")
        binding.dateChip.transitionName = resources.getString(R.string.note_date_transition_name,
                id?.toString() ?: "local")
        binding.photosList.transitionName = resources.getString(R.string.note_photos_transition_name,
                id?.toString() ?: "local")
        binding.backButton.setOnClickListener { parentFragmentManager.popBackStack() }

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

        binding.photosList.layoutManager = GridLayoutManager(context, 1,
                RecyclerView.HORIZONTAL, false)
        binding.photosList.addItemDecoration(
                PaddingDecoration(
                        8f.toPx(resources),
                        8f.toPx(resources),
                        8f.toPx(resources),
                        8f.toPx(resources)
                ))
        binding.photosList.adapter = adapter

        viewModel.note().observe(viewLifecycleOwner, { note ->
            note.run {
                binding.textTitle.text = title
                binding.textTitleActionBar.text = title
                binding.textDescription.text = text
                binding.dateChip.setDate(date)
                setupEditButton(this)
                startPostponedEnterTransition()
                binding.deleteButton.setOnClickListener {
                    viewModel.delete()
                }
                adapter.updateData(note.photos.map { it.path })
                binding.photosList.visibility = if (note.photos.isEmpty()) View.GONE else View.VISIBLE
            }
        })
        viewModel.error().observe(viewLifecycleOwner, { error ->
        })
        viewModel.deleteResult().observe(viewLifecycleOwner, { status ->
            if (status == true) {
                setFragmentResult(EditUtils.DELETE_NOTE_REQUEST_KEY, Bundle())
                sharedElementReturnTransition = null
                parentFragmentManager.popBackStack()
            }
        })
        id?.run { viewModel.loadNote(this) }
    }

    private fun setupEditButton(note: Note) {
        binding.editButton.setOnClickListener {
            note.id?.run {
                val nextFragment = EditNoteFragment.initMe(this,
                        scrollY = binding.noteContent.scrollY)

                nextFragment.sharedElementEnterTransition = TransitionSet()
                        .apply {
                            addTransition(TransitionInflater.from(context)
                                    .inflateTransition(android.R.transition.move))
                            duration = resources.getInteger(R.integer.animation_duration).toLong()
                        }

                parentFragmentManager
                        .beginTransaction()
                        .setReorderingAllowed(true)
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in,
                                R.anim.fade_out)
                        .replace(R.id.container, nextFragment)
                        .addSharedElement(binding.dateChip, binding.dateChip.transitionName)
                        .addToBackStack(null)
                        .commit()
            }
        }
    }

    override fun onStartSharedAnimation(sharedElementNames: MutableList<String>) {
        super.onStartSharedAnimation(sharedElementNames)
        binding.deleteButton.onLayout {
            binding.deleteButton.visibility = View.GONE
            binding.deleteButton.animateSlideTopVisibility(true)
        }
        binding.editButton.onLayout {
            binding.editButton.visibility = View.GONE
            binding.editButton.animateSlideTopVisibility(true)
        }
        binding.backButton.onLayout {
            binding.backButton.visibility = View.GONE
            binding.backButton.animateSlideTopVisibility(true)
        }
    }

    companion object {
        const val BACK_STACK_TAG = "ShowNote.BACK_STACK_TAG"
        private const val EXTRA_ID = "EXTRA_ID"
        private const val EXTRA_FROM_ARCHIVE = "EXTRA_FROM_ARCHIVE"

        fun initMe(id: Long, fromArchive: Boolean = false): ShowNoteFragment {
            val fragment = ShowNoteFragment()
            val args = Bundle()
            args.putLong(EXTRA_ID, id)
            args.putBoolean(EXTRA_FROM_ARCHIVE, fromArchive)
            fragment.arguments = args
            return fragment
        }
    }
}