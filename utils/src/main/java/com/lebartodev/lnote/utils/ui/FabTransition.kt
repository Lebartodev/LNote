package com.lebartodev.lnote.utils.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.View.MeasureSpec.makeMeasureSpec
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import androidx.transition.Transition
import androidx.transition.TransitionValues
import com.google.android.material.floatingactionbutton.FloatingActionButton


private const val PROPERTY_COLOR = "lnote:fabTransform:color"
private const val PROPERTY_BOUNDS = "lnote:fabTransform:bounds"

class FabTransition : Transition() {

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
            transitionValues.values[PROPERTY_BOUNDS] = Rect(
                transitionValues.view.left, transitionValues.view.top,
                transitionValues.view.right, transitionValues.view.bottom
            )
        }
    }

    private fun captureColor(transitionValues: TransitionValues) {
        if (transitionValues.view is ViewGroup) {
            val background = transitionValues.view.background
            if (background is ColorDrawable)
                transitionValues.values[PROPERTY_COLOR] = background.color
        } else if (transitionValues.view is FloatingActionButton) {
            transitionValues.values[PROPERTY_COLOR] =
                (transitionValues.view as FloatingActionButton).backgroundTintList?.defaultColor
        }
    }

    @Suppress("Detekt.ReturnCount")
    override fun createAnimator(
        sceneRoot: ViewGroup,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {
        if (startValues == null || endValues == null) return null

        val startColor = (startValues.values[PROPERTY_COLOR] as Int?) ?: Color.WHITE
        val endColor = (endValues.values[PROPERTY_COLOR] as Int?) ?: Color.WHITE

        val startBounds = startValues.values[PROPERTY_BOUNDS] as Rect?
        val endBounds = endValues.values[PROPERTY_BOUNDS] as Rect?

        if (startBounds == null || endBounds == null) return null

        val fromFab = endBounds.width() > startBounds.width()
        var processView = endValues.view
        val processViewTranslationY = endValues.view.translationY

        if (!fromFab) {
            val layout = processView.parent as ViewGroup
            val placeholderView = View(sceneRoot.context)
            placeholderView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            layout.addView(placeholderView)
            processView = placeholderView

            processView.measure(
                makeMeasureSpec(startBounds.width(), View.MeasureSpec.EXACTLY),
                makeMeasureSpec(startBounds.height(), View.MeasureSpec.EXACTLY)
            )
            processView.layout(
                startBounds.left,
                startBounds.top,
                startBounds.right,
                startBounds.bottom
            )
        }

        val overlayAnimation = createOverlayAnimation(processView, startColor, endColor)
        val circularReveal = createCircularRevealAnimation(
            processView,
            fromFab,
            startBounds,
            endBounds,
            processViewTranslationY
        )


        val transition = AnimatorSet()
        transition.playTogether(overlayAnimation, circularReveal)

        transition.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                processView.translationY = processViewTranslationY
            }
        })

        return transition
    }

    private fun createOverlayAnimation(
        processView: View,
        startColor: Int,
        endColor: Int
    ): Animator {
        val fabColor = ColorDrawable(startColor)
        fabColor.setBounds(0, 0, processView.width, processView.height)
        processView.background = fabColor
        return ObjectAnimator.ofArgb(fabColor, "color", endColor).setDuration(duration / 2)
    }

    private fun createCircularRevealAnimation(
        processView: View,
        fromFab: Boolean,
        startBounds: Rect,
        endBounds: Rect,
        processViewTranslationY: Float
    ): Animator {
        val circularReveal: Animator
        if (fromFab) {
            circularReveal = ViewAnimationUtils.createCircularReveal(
                processView,
                startBounds.centerX(),
                startBounds.centerY(),
                startBounds.height() / 2f,
                endBounds.height().toFloat().coerceAtLeast(endBounds.width().toFloat())
            )
        } else {
            circularReveal = ViewAnimationUtils.createCircularReveal(
                processView,
                endBounds.centerX(),
                endBounds.centerY() + processViewTranslationY.toInt(),
                startBounds.height().toFloat(),
                0f
            )

            circularReveal.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    (processView.parent as ViewGroup).removeView(processView)
                }
            })
        }
        return circularReveal
    }
}