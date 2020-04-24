package com.lebartodev.lnote.utils.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.transition.ChangeBounds
import androidx.transition.TransitionValues
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.lebartodev.lnote.R


private const val PROPERTY_CORNER_RADIUS = "PROPERTY_CORNER_RADIUS"
private const val PROPERTY_ELEVATION = "PROPERTY_ELEVATION"
private const val PROPERTY_COLOR = "PROPERTY_COLOR"


class CardExpandTransition : ChangeBounds {
    constructor() : super()
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun getTransitionProperties(): Array<String> {
        return arrayOf(PROPERTY_CORNER_RADIUS, PROPERTY_ELEVATION)
    }

    override fun captureStartValues(transitionValues: TransitionValues) {
        super.captureStartValues(transitionValues)
        captureValues(transitionValues)
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        super.captureEndValues(transitionValues)
        captureValues(transitionValues)

    }

    private fun captureValues(transitionValues: TransitionValues) {
        captureElevation(transitionValues)
        captureCornerRadius(transitionValues)
        captureBackground(transitionValues)
    }


    private fun captureElevation(transitionValues: TransitionValues) {
        transitionValues.values[PROPERTY_ELEVATION] = transitionValues.view.elevation
    }

    private fun captureCornerRadius(transitionValues: TransitionValues) {
        if (transitionValues.view is CardView) {
            transitionValues.values[PROPERTY_CORNER_RADIUS] = (transitionValues.view as CardView).radius
        } else if (transitionValues.view is FloatingActionButton) {
            transitionValues.values[PROPERTY_CORNER_RADIUS] = transitionValues.view.height / 2f
        } else {
            transitionValues.values[PROPERTY_CORNER_RADIUS] = 0f
        }
    }

    private fun captureBackground(transitionValues: TransitionValues) {
        if (transitionValues.view is ViewGroup) {
            val background = transitionValues.view.background
            if (background is ColorDrawable)
                transitionValues.values[PROPERTY_COLOR] = background.color
            if (background is NoteTransitionDrawable)
                transitionValues.values[PROPERTY_COLOR] = background.getColor()
        } else if (transitionValues.view is FloatingActionButton) {
            transitionValues.values[PROPERTY_COLOR] = (transitionValues.view as FloatingActionButton).backgroundTintList?.defaultColor
        }
    }

    override fun createAnimator(sceneRoot: ViewGroup, startValues: TransitionValues?, endValues: TransitionValues?): Animator? {
        val changeBounds = super.createAnimator(sceneRoot, startValues, endValues)
        if (startValues == null || endValues == null)
            return null

        val startElevation = startValues.values[PROPERTY_ELEVATION] as Float
        val endElevation = endValues.values[PROPERTY_ELEVATION] as Float

        val startCornerRadius = startValues.values[PROPERTY_CORNER_RADIUS] as Float
        val endCornerRadius = endValues.values[PROPERTY_CORNER_RADIUS] as Float

        val startColor = (startValues.values[PROPERTY_COLOR] as Int?) ?: Color.WHITE
        val endColor = (endValues.values[PROPERTY_COLOR] as Int?) ?: Color.WHITE

        if (startElevation != 0f) {
            endValues.view.elevation = startElevation
        } else {
            startValues.view.elevation = endElevation
        }

        val background = NoteTransitionDrawable(startColor, startCornerRadius)
        background.setBounds(0, 0, startValues.view.width, startValues.view.height)
        endValues.view.overlay.add(background)

        val color = ObjectAnimator.ofArgb(background, NoteTransitionDrawable.COLOR, endColor)
        val radius = ObjectAnimator.ofFloat(background, NoteTransitionDrawable.CORNER_RADIUS, endCornerRadius)
        val elevation = ObjectAnimator.ofFloat(endValues.view, "elevation", endElevation)

        val transition = AnimatorSet()
        transition.playTogether(changeBounds, elevation, color, radius)
        transition.duration = sceneRoot.resources.getInteger(R.integer.animation_duration).toLong()

        transition.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                endValues.view.overlay.clear()
            }
        })
        return transition
    }
}