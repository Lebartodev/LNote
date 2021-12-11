package com.lebartodev.core.base

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import androidx.core.app.SharedElementCallback
import androidx.fragment.app.Fragment
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


abstract class BaseFragment : Fragment() {
    var isSharedAnimationEnd = false

    fun hideKeyboard(additionalEditText: View? = null) {
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
            view?.getWindowVisibleDisplayFrame(r)
            val heightDiff = (view?.rootView?.height ?: 0) - (r.bottom - r.top)
            return heightDiff > 500
        }

        val listener = object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (!isKeyboardVisible()) {
                    keyboardListener.invoke()
                }
                view?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
            }
        }
        if (!isKeyboardVisible()) {
            view?.viewTreeObserver?.removeOnGlobalLayoutListener(listener)
            keyboardListener.invoke()
        } else {
            view?.viewTreeObserver?.addOnGlobalLayoutListener(listener)
            hideKeyboard(additionalEditText)
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (sharedElementEnterTransition == null)
            isSharedAnimationEnd = true
        else
            setEnterSharedElementCallback(object : SharedElementCallback() {
                override fun onSharedElementStart(sharedElementNames: MutableList<String>?, sharedElements: MutableList<View>?, sharedElementSnapshots: MutableList<View>?) {
                    super.onSharedElementStart(sharedElementNames, sharedElements, sharedElementSnapshots)
                    onStartSharedAnimation(sharedElementNames ?: arrayListOf())
                    isSharedAnimationEnd = false
                }

                override fun onSharedElementEnd(sharedElementNames: MutableList<String>?, sharedElements: MutableList<View>?, sharedElementSnapshots: MutableList<View>?) {
                    super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots)
                    isSharedAnimationEnd = true
                    setEnterSharedElementCallback(null)
                }

                override fun onMapSharedElements(names: MutableList<String>?, sharedElements: MutableMap<String, View>?) {
                    super.onMapSharedElements(names, sharedElements)
                    isSharedAnimationEnd = false
                }
            })
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