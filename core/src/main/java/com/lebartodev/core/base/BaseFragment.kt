package com.lebartodev.core.base

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import androidx.core.app.SharedElementCallback
import androidx.fragment.app.Fragment
import com.lebartodev.core.di.utils.ViewModelFactory
import javax.inject.Inject
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


abstract class BaseFragment : Fragment() {
    protected var isSharedAnimationEnd = false
    protected abstract val fragmentView: View

    @Inject
    protected lateinit var viewModelFactory: ViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return fragmentView
    }

    protected fun hideKeyboard(additionalEditText: View? = null) {
        val imm = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity?.currentFocus
        if (additionalEditText != null) {
            view = additionalEditText
        }
        if (view == null) {
            view = View(context)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun hideKeyboardListener(additionalEditText: View? = null, keyboardListener: () -> Unit) {

        fun isKeyboardVisible(): Boolean {
            val r = Rect()
            fragmentView?.getWindowVisibleDisplayFrame(r)
            val heightDiff = (fragmentView?.rootView?.height ?: 0) - (r.bottom - r.top)
            return heightDiff > 500
        }

        val listener = object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (!isKeyboardVisible()) {
                    keyboardListener.invoke()
                }
                fragmentView?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
            }
        }
        if (!isKeyboardVisible()) {
            fragmentView?.viewTreeObserver?.removeOnGlobalLayoutListener(listener)
            keyboardListener.invoke()
        } else {
            fragmentView?.viewTreeObserver?.addOnGlobalLayoutListener(listener)
            hideKeyboard(additionalEditText)
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (sharedElementEnterTransition == null) {
            isSharedAnimationEnd = true
        } else {
            setEnterSharedElementCallback(object : SharedElementCallback() {
                override fun onSharedElementStart(
                    names: MutableList<String>?,
                    elements: MutableList<View>?,
                    snapshots: MutableList<View>?
                ) {
                    super.onSharedElementStart(names, elements, snapshots)
                    onStartSharedAnimation(names ?: arrayListOf())
                    isSharedAnimationEnd = false
                }

                override fun onSharedElementEnd(
                    names: MutableList<String>?,
                    elements: MutableList<View>?,
                    snapshots: MutableList<View>?
                ) {
                    super.onSharedElementEnd(names, elements, snapshots)
                    isSharedAnimationEnd = true
                    setEnterSharedElementCallback(null)
                }

                override fun onMapSharedElements(
                    names: MutableList<String>?,
                    sharedElements: MutableMap<String, View>?
                ) {
                    super.onMapSharedElements(names, sharedElements)
                    isSharedAnimationEnd = false
                }
            })
        }
    }

    open fun onStartSharedAnimation(sharedElementNames: MutableList<String>) {
    }
}

class FragmentArgumentDelegate<T : Any> : ReadOnlyProperty<Fragment, T> {
    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        val key = property.name
        return thisRef.arguments?.get(key) as? T
            ?: throw IllegalStateException("Property ${property.name} could not be read")
    }
}

class FragmentNullableArgumentDelegate<T : Any?> : ReadOnlyProperty<Fragment, T?> {

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Fragment, property: KProperty<*>): T? {
        val key = property.name
        return thisRef.arguments?.get(key) as? T
    }
}

fun <T : Any> fragmentArgs(): ReadOnlyProperty<Fragment, T> =
    FragmentArgumentDelegate()

fun <T : Any> fragmentNullableArgs(): ReadOnlyProperty<Fragment, T?> =
    FragmentNullableArgumentDelegate()