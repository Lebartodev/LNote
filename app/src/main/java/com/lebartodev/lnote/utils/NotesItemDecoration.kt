package com.lebartodev.lnote.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class NotesItemDecoration(val topPadding: Float,
                          val bottomPadding: Float,
                          val leftPadding: Float,
                          val rightPadding: Float) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.bottom = bottomPadding.toInt()
        outRect.top = topPadding.toInt()
        outRect.left = leftPadding.toInt()
        outRect.right = rightPadding.toInt()
    }
}