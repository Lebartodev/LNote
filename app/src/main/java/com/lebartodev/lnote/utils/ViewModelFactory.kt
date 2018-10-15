package com.lebartodev.lnote.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider


class ViewModelFactory : ViewModelProvider.Factory {
    private var viewModels: Map<Class<out ViewModel>,  Provider<ViewModel>>

    @Inject constructor(viewModels: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>) {
        this.viewModels = viewModels
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModelProvider = viewModels[modelClass] ?: throw IllegalArgumentException("model class "
                + modelClass
                + " not found")

        return viewModelProvider.get() as T
    }
}