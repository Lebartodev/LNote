package com.lebartodev.lnote.utils

import androidx.recyclerview.widget.DiffUtil
import com.lebartodev.lnote.data.entity.Note

class NoteDiffUtilCallback(private val oldList: List<Note>, private val newList: List<Note>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val (id) = oldList[oldItemPosition]
        val (id1) = newList[newItemPosition]
        return id === id1
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldNote = oldList[oldItemPosition]
        val newNote = newList[newItemPosition]
        return oldNote == newNote
    }

}