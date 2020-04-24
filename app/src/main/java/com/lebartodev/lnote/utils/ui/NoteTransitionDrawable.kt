package com.lebartodev.lnote.utils.ui

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.Property


class NoteTransitionDrawable(color: Int, cornerRadius: Float) : Drawable() {
    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    var cornerRadius: Float = cornerRadius
        set(radius) {
            field = radius
            invalidateSelf()
        }

    init {
        paint.color = color
    }

    fun getColor() = paint.color

    fun setColor(color: Int) {
        paint.color = color
        invalidateSelf()
    }

    override fun draw(canvas: Canvas) {
        canvas.drawRoundRect(bounds.left.toFloat(), bounds.top.toFloat(), bounds.right.toFloat(), bounds.bottom.toFloat(), cornerRadius, cornerRadius, paint)
    }

    override fun getOutline(outline: Outline) {
        outline.setRoundRect(bounds, cornerRadius)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
        invalidateSelf()
    }

    override fun setColorFilter(cf: ColorFilter) {
        paint.colorFilter = cf
        invalidateSelf()
    }

    override fun getOpacity(): Int {
        return paint.alpha
    }

    companion object {
        val CORNER_RADIUS: Property<NoteTransitionDrawable, Float> = object : Property<NoteTransitionDrawable, Float>(Float::class.java, "cornerRadius") {
            override operator fun set(drawable: NoteTransitionDrawable, value: Float) {
                drawable.cornerRadius = value
            }

            override operator fun get(drawable: NoteTransitionDrawable): Float {
                return drawable.cornerRadius
            }
        }
        val COLOR: Property<NoteTransitionDrawable, Int> = object : Property<NoteTransitionDrawable, Int>(Int::class.java, "color") {
            override operator fun set(drawable: NoteTransitionDrawable, value: Int) {
                drawable.setColor(value)
            }

            override operator fun get(drawable: NoteTransitionDrawable): Int {
                return drawable.getColor()
            }
        }
    }
}