package com.lebartodev.lnote.settings

import android.content.Context
import android.util.AttributeSet
import android.widget.Switch
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.lebartodev.core.di.utils.AppComponentProvider
import com.lebartodev.lnote.settings.di.DaggerSettingsComponent
import javax.inject.Inject

class SettingsBottomView : ConstraintLayout {
    @Inject
    lateinit var viewModelFactory: SettingsViewModelFactory
    private lateinit var notesViewModel: SettingsViewModel
    private val bottomPanelSwitch: Switch

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        inflate(context, R.layout.view_settings, this)
        bottomPanelSwitch = findViewById(R.id.bottom_panel_switch)
        DaggerSettingsComponent.builder()
                .context(context)
                .appComponent((context.applicationContext as AppComponentProvider).provideAppComponent())
                .build()
                .inject(this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        notesViewModel = ViewModelProvider(context as FragmentActivity, viewModelFactory)[SettingsViewModel::class.java]

        notesViewModel.bottomPanelEnabled().observe(context as FragmentActivity, Observer { bottomPanelSwitch.isChecked = it })

        bottomPanelSwitch.setOnCheckedChangeListener { _, b -> notesViewModel.setBottomPanelEnabled(b) }
    }
}