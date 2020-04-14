package com.lebartodev.lnote.common.notes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.lebartodev.lnote.R
import com.lebartodev.lnote.data.entity.Note
import java.text.SimpleDateFormat
import java.util.*


class NotesAdapter(private val listener: (note: Note, sharedView: View) -> Unit) : RecyclerView.Adapter<NotesAdapter.ViewHolder>() {
    val data: MutableList<Note> = mutableListOf()

    init {
        setHasStableIds(true)
    }

    fun updateData(notes: List<Note>) {
        data.clear()
        data.addAll(notes)
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return data[position].id ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.i_note, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(data[position], listener)

    override fun getItemCount() = data.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val formatter = SimpleDateFormat(itemView.resources.getString(R.string.date_pattern), Locale.US)
        val title: TextView = itemView.findViewById(R.id.note_title)
        val description: TextView = itemView.findViewById(R.id.note_description)
        val dateChip: Chip = itemView.findViewById(R.id.note_date_chip)
        val noteContent: View = itemView.findViewById(R.id.note_item_content)
        fun bind(item: Note, listener: (note: Note, sharedView: View) -> Unit) {
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
            itemView.setOnClickListener {
                listener(item, noteContent)
            }
            noteContent.transitionName = itemView.resources.getString(R.string.note_content_transition_name, item.id)
        }
    }

    companion object {
        private const val MAX_LINES = 6
    }
}