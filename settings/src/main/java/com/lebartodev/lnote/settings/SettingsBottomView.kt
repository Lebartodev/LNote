package com.lebartodev.lnote.settings

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.lebartodev.core.di.utils.CoreComponentProvider
import com.lebartodev.lnote.settings.databinding.ViewSettingsBinding
import com.lebartodev.lnote.settings.di.DaggerSettingsComponent
import javax.inject.Inject

class SettingsBottomView : ConstraintLayout {
    @Inject
    lateinit var viewModelFactory: SettingsViewModelFactory
    private lateinit var notesViewModel: SettingsViewModel
    private val viewBinding = ViewSettingsBinding.inflate(LayoutInflater.from(context), this)

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs,
            defStyleAttr)

    init {
        DaggerSettingsComponent.builder()
                .coreComponent((context.applicationContext as CoreComponentProvider).coreComponent)
                .build()
                .inject(this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        notesViewModel = ViewModelProvider(context as FragmentActivity,
                viewModelFactory)[SettingsViewModel::class.java]

        notesViewModel.bottomPanelEnabled()
                .observe(context as FragmentActivity) {
                    viewBinding.bottomPanelSwitch.isChecked = it
                }
        viewBinding.bottomPanelSwitch.setOnCheckedChangeListener { _, b ->
            notesViewModel.setBottomPanelEnabled(b)
        }
    }
}