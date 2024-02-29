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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.lebartodev.core.base.BaseFragment
import com.lebartodev.core.data.NoteData
import com.lebartodev.core.utils.viewBinding
import com.lebartodev.lnote.edit.R
import com.lebartodev.lnote.edit.databinding.FragmentNoteCreationContainerBinding
import com.lebartodev.lnote.edit.utils.EditUtils
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
        binding.bottomSheetAdd.post {
            bottomAddSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        binding.root.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN)
                bottomAddSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            true
        }
        bottomAddSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED || newState == BottomSheetBehavior.STATE_HIDDEN) {
                    hideKeyboard(bottomSheet)
                    parentFragmentManager.popBackStack()
                }
            }
        })
        binding.bottomSheetAdd.apply {
            closeListener = {
                bottomAddSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
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

    companion object {
        private const val TAG = "NoteCreationContainerFragment"
        private const val EXTRA_NOTE_DATA = "EXTRA_NOTE_DATA"

        fun initMe(): NoteCreationContainerFragment {
            return NoteCreationContainerFragment()
        }
    }
}