package com.lebartodev.lnote.feature_attach.di

import android.content.Context
import com.lebartodev.core.di.app.AppComponent
import com.lebartodev.lnote.feature_attach.AttachPanelFragment
import dagger.BindsInstance
import dagger.Component

@AttachScope
@Component(dependencies = [AppComponent::class], modules = [AttachModule::class])
interface AttachComponent {
    fun inject(attachPanelFragment: AttachPanelFragment)

    @Component.Builder
    interface Builder {
        fun build(): AttachComponent

        @BindsInstance
        fun context(context: Context): Builder

        fun appComponent(appComponent: AppComponent): Builder
    }
}