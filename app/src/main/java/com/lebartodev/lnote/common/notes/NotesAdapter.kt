package com.lebartodev.lnote.common.notes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.lebartodev.lnote.R
import com.lebartodev.lnote.data.entity.Note
import com.lebartodev.lnote.utils.NoteDiffUtilCallback
import java.text.SimpleDateFormat
import java.util.*


class NotesAdapter(private val listener: (note: Note) -> Unit) : RecyclerView.Adapter<NotesAdapter.ViewHolder>() {
    val data: MutableList<Note> = mutableListOf()

    init {
        setHasStableIds(true)
    }

    fun updateData(notes: List<Note>) {
        val result = DiffUtil.calculateDiff(NoteDiffUtilCallback(data, notes))
        data.clear()
        data.addAll(notes)
        result.dispatchUpdatesTo(this)
    }

    override fun getItemId(position: Int): Long {
        return data.get(position).id ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.i_note, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(data[position])

    override fun getItemCount() = data.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val formatter = SimpleDateFormat("EEE, dd MMM yyyy", Locale.US)
        val title: TextView = itemView.findViewById(R.id.note_title)
        val description: TextView = itemView.findViewById(R.id.note_description)
        val dateChip: Chip = itemView.findViewById(R.id.note_date_chip)
        fun bind(item: Note) = with(itemView) {
            title.text = item.title
            val lines = item.text.split("\n")

            if (lines.size > MAX_LINES) {
                var text = ""
                for (index in 0..MAX_LINES) {
                    text += (lines[index])
                    text += "\n"
                }
                text += ("...")
                description.text = text
            } else
                description.text = item.text


            if (item.date != null) {
                dateChip.text = formatter.format(item.date)
                dateChip.visibility = View.VISIBLE
            } else {
                dateChip.visibility = View.GONE
            }
            this.setOnClickListener {
                listener(item)
            }
        }
    }

    companion object {
        private const val MAX_LINES = 6
    }
}