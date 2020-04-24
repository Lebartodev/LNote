package com.lebartodev.lnote.utils.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Color
import android.graphics.Outline
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.View.MeasureSpec.makeMeasureSpec
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.core.content.ContextCompat
import androidx.transition.Transition
import androidx.transition.TransitionValues
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.lebartodev.lnote.R
import kotlin.math.hypot


private const val PROPERTY_COLOR = "lnote:fabTransform:color"
private const val PROPERTY_BOUNDS = "lnote:fabTransform:bounds"

class FabTransition : Transition {
    constructor() : super()

    override fun getTransitionProperties(): Array<String> {
        return arrayOf(PROPERTY_COLOR, PROPERTY_BOUNDS)
    }

    override fun captureStartValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    private fun captureValues(transitionValues: TransitionValues) {
        captureBounds(transitionValues)
        captureColor(transitionValues)
    }

    private fun captureBounds(transitionValues: TransitionValues) {
        if (transitionValues.view.width > 0 && transitionValues.view.height > 0) {
            transitionValues.values[PROPERTY_BOUNDS] = Rect(transitionValues.view.left, transitionValues.view.top,
                    transitionValues.view.right, transitionValues.view.bottom)
        }
    }

    private fun captureColor(transitionValues: TransitionValues) {
        if (transitionValues.view is ViewGroup) {
            val background = transitionValues.view.background
            if (background is ColorDrawable)
                transitionValues.values[PROPERTY_COLOR] = background.color
        } else if (transitionValues.view is FloatingActionButton) {
            transitionValues.values[PROPERTY_COLOR] = (transitionValues.view as FloatingActionButton).backgroundTintList?.defaultColor
        }
    }

    override fun createAnimator(sceneRoot: ViewGroup, startValues: TransitionValues?, endValues: TransitionValues?): Animator? {
        if (startValues == null || endValues == null) return null

        val startColor = (startValues.values[PROPERTY_COLOR] as Int?) ?: Color.WHITE
        val endColor = (endValues.values[PROPERTY_COLOR] as Int?) ?: Color.WHITE

        val startBounds = startValues.values[PROPERTY_BOUNDS] as Rect?
        val endBounds = endValues.values[PROPERTY_BOUNDS] as Rect?

        if (startBounds == null || endBounds == null) return null

        val fromFab = endBounds.width() > startBounds.width()
        val processView = endValues.view
        processView.isClickable = false

        val processViewTranslationY = processView.translationY
        val endTranslationY: Float

        if (!fromFab) {
            endTranslationY = (endBounds.centerY() - startBounds.centerY()).toFloat() + processViewTranslationY

            processView.measure(makeMeasureSpec(startBounds.width(), View.MeasureSpec.EXACTLY),
                    makeMeasureSpec(startBounds.height(), View.MeasureSpec.EXACTLY))
            processView.layout(startBounds.left, startBounds.top, startBounds.right, startBounds.bottom)
        } else {
            endTranslationY = processViewTranslationY

            processView.translationY = (startBounds.centerY() - endBounds.centerY()).toFloat() + processViewTranslationY
        }

        val translate: Animator = ObjectAnimator.ofFloat(processView, View.TRANSLATION_Y, endTranslationY)

        val circularReveal: Animator
        if (fromFab) {
            circularReveal = ViewAnimationUtils.createCircularReveal(processView,
                    processView.width / 2,
                    processView.height / 2,
                    startBounds.width() / 2f,
                    hypot(endBounds.width() / 2f, endBounds.height() / 2f))
        } else {
            circularReveal = ViewAnimationUtils.createCircularReveal(processView,
                    processView.width / 2,
                    processView.height / 2,
                    hypot(startBounds.width() / 2f, startBounds.height() / 2f),
                    endBounds.width() / 2f)

            //Persist the end clip i.e. stay at FAB size after the reveal has run
            circularReveal.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    processView.outlineProvider = object : ViewOutlineProvider() {
                        override fun getOutline(view: View, outline: Outline) {
                            val left: Int = (view.width - endBounds.width()) / 2
                            val top: Int = (view.height - endBounds.height()) / 2
                            outline.setOval(left, top, left + endBounds.width(), top + endBounds.height())
                            view.clipToOutline = true
                        }
                    }
                }
            })
        }


        val fabColor = ColorDrawable(startColor)
        fabColor.setBounds(0, 0, processView.width, processView.height)
        processView.overlay.add(fabColor)


        val fabIcon = ContextCompat.getDrawable(sceneRoot.context, R.drawable.ic_add_24)?.mutate()!!
        val iconLeft = (processView.width - fabIcon.intrinsicWidth) / 2
        val iconTop = (processView.height - fabIcon.intrinsicHeight) / 2
        if (!fromFab) fabIcon.alpha = 0

        fabIcon.setBounds(iconLeft, iconTop,
                iconLeft + fabIcon.intrinsicWidth,
                iconTop + fabIcon.intrinsicHeight)

        processView.overlay.add(fabIcon)


        val colorTransition = ObjectAnimator.ofArgb(fabColor, "color", endColor)
        colorTransition.duration = duration / 2


        val alphaOverlay = ObjectAnimator.ofInt(fabColor, "alpha", 255, 0)
        alphaOverlay.startDelay = duration / 2
        alphaOverlay.duration = duration / 2


        val iconAlpha = ObjectAnimator.ofInt(fabIcon, "alpha", if (fromFab) 0 else 255)
        iconAlpha.duration = duration / 2
        iconAlpha.startDelay = duration / 2

        val transition = AnimatorSet()
        transition.playTogether(circularReveal, translate, colorTransition, alphaOverlay, iconAlpha)

        transition.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                processView.translationY = processViewTranslationY
                processView.measure(makeMeasureSpec(endBounds.width(), View.MeasureSpec.EXACTLY),
                        makeMeasureSpec(endBounds.height(), View.MeasureSpec.EXACTLY))
                processView.layout(endBounds.left, endBounds.top, endBounds.right, endBounds.bottom)
                processView.overlay.clear()
                processView.isClickable = true
            }
        })

        return transition
    }

}