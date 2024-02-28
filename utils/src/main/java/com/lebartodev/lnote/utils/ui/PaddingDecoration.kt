package com.lebartodev.lnote.utils.ui

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class PaddingDecoration(val topPadding: Float,
                        val bottomPadding: Float,
                        val leftPadding: Float,
                        val rightPadding: Float) : RecyclerView.ItemDecoration() {
    constructor(padding: Float) : this(padding, padding, padding, padding)

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
                                state: RecyclerView.State) {
        outRect.bottom = bottomPadding.toInt()
        outRect.top = topPadding.toInt()
        outRect.left = leftPadding.toInt()
        outRect.right = rightPadding.toInt()
    }
}