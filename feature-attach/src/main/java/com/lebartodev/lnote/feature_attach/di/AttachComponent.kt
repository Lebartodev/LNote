package com.lebartodev.lnote.feature_attach.di

import com.lebartodev.core.di.app.CoreComponent
import com.lebartodev.core.di.utils.FeatureScope
import com.lebartodev.lnote.feature_attach.ui.AttachPanelFragment
import dagger.Component

@FeatureScope
@Component(
    dependencies = [CoreComponent::class],
    modules = [AttachModule::class]
)
interface AttachComponent {
    fun inject(attachPanelFragment: AttachPanelFragment)
}