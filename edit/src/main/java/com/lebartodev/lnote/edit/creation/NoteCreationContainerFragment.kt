package com.lebartodev.lnote.edit.creation

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.setFragmentResultListener
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import androidx.transition.TransitionInflater
import androidx.transition.TransitionSet
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.lebartodev.core.base.BaseFragment
import com.lebartodev.core.data.NoteData
import com.lebartodev.core.utils.viewBinding
import com.lebartodev.lnote.edit.EditNoteFragment
import com.lebartodev.lnote.edit.R
import com.lebartodev.lnote.edit.databinding.FragmentNoteCreationContainerBinding
import com.lebartodev.lnote.edit.utils.EditUtils
import com.lebartodev.lnote.utils.ui.CardExpandTransition
import com.lebartodev.lnote.utils.ui.LockableBottomSheetBehavior
import java.util.*

class NoteCreationContainerFragment : BaseFragment() {
    private val binding by viewBinding(FragmentNoteCreationContainerBinding::inflate)
    private lateinit var bottomAddSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomAddSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetAdd)
        bottomAddSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomAddSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    hideKeyboard(bottomSheet)
                    parentFragmentManager.popBackStack()
                }
            }
        })
        binding.bottomSheetAdd.apply {
            closeListener = {
                bottomAddSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
            fullScreenListener = { openFullScreen() }
        }

        val noteContent = binding.bottomSheetAdd.findViewById<NestedScrollView>(R.id.note_content)
        noteContent.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_MOVE) {
                if (noteContent.scrollY != 0) {
                    (bottomAddSheetBehavior as LockableBottomSheetBehavior).swipeEnabled = false
                }
            }
            (bottomAddSheetBehavior as LockableBottomSheetBehavior).swipeEnabled = true
            return@setOnTouchListener false
        }
        setFragmentResultListener(EditUtils.SAVE_NOTE_REQUEST_KEY) { _, _ ->
            bottomAddSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun openFullScreen() {
        hideKeyboardListener(binding.bottomSheetAdd.findFocus()) {
            val nextFragment = EditNoteFragment.initMe(
                forceBackButtonVisible = false,
                scrollY = binding.bottomSheetAdd.getContentScroll()
            )
                .apply {
                    sharedElementEnterTransition = TransitionSet()
                        .apply {
                            addTransition(
                                TransitionInflater.from(this@NoteCreationContainerFragment.context)
                                    .inflateTransition(android.R.transition.move))
                            addTransition(CardExpandTransition())
                            duration = this@NoteCreationContainerFragment.resources.getInteger(
                                R.integer.animation_duration).toLong()
                            interpolator = LinearOutSlowInInterpolator()
                        }
                }

            parentFragmentManager.beginTransaction().run {
                setReorderingAllowed(true)
                setCustomAnimations(0, R.anim.fade_out, 0, R.anim.fade_out)
                binding.bottomSheetAdd.getSharedViews()
                    .forEach { addSharedElement(it, it.transitionName) }
                replace(R.id.edit_container, nextFragment)
                addToBackStack(null)
                commit()
            }
        }
    }

    companion object {
        private const val EXTRA_NOTE_DATA = "EXTRA_NOTE_DATA"

        fun initMe(noteData: NoteData = NoteData()): NoteCreationContainerFragment {
            val fragment = NoteCreationContainerFragment()
            fragment.arguments = Bundle()
                .apply { putParcelable(EXTRA_NOTE_DATA, noteData) }
            return fragment
        }
    }
}