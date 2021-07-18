package com.lebartodev.lnote.feature_attach

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lebartodev.core.di.utils.AppComponentProvider
import com.lebartodev.core.utils.viewBinding
import com.lebartodev.lnote.feature_attach.databinding.FragmentAttachPanelBinding
import com.lebartodev.lnote.feature_attach.di.DaggerAttachComponent
import javax.inject.Inject


class AttachPanelFragment : BottomSheetDialogFragment() {
    private val adapter = PhotosAdapter { viewModel.attachPhoto(it) }
    private val binding by viewBinding(FragmentAttachPanelBinding::inflate)

    @Inject
    lateinit var viewModelFactory: AttachViewModelFactory
    private val viewModel: AttachViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[AttachViewModel::class.java]
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerAttachComponent.builder()
                .context(context)
                .appComponent(
                        (context.applicationContext as AppComponentProvider).provideAppComponent())
                .build()
                .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        binding.list.layoutManager = GridLayoutManager(context, 3)
        binding.list.adapter = adapter
        viewModel.photos.observe(viewLifecycleOwner, {
            adapter.updateData(it)
        })
        viewModel.selectedPhoto.observe(viewLifecycleOwner) {
            setFragmentResult(ATTACH_REQUEST_KEY, bundleOf(PHOTO_PATH to it))
            dismiss()
        }

        if (checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(listOf(Manifest.permission.READ_EXTERNAL_STORAGE).toTypedArray(), 1);

            return;
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty()
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    viewModel.loadPhotos()
                } else {
                    Toast.makeText(requireContext(),
                            "Permission denied", Toast.LENGTH_SHORT)
                            .show()
                }
                return
            }
        }
    }

    companion object {
        const val TAG = "AttachPanelFragment"
        const val ATTACH_REQUEST_KEY = "ATTACH_REQUEST_KEY"
        const val PHOTO_PATH = "PHOTO_PATH"
    }
}