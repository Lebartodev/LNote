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

class NotesAdapter : RecyclerView.Adapter<NotesAdapter.ViewHolder> {
    private var listener: OpenNoteListener? = null
    var data: List<Note> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    constructor(listener: OpenNoteListener) {
        setHasStableIds(true)
        this.listener = listener
    }

    override fun getItemId(position: Int): Long {
        return data.get(position).id ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.i_note, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(data[position], listener)

    override fun getItemCount() = data.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val formatter = SimpleDateFormat("EEE, dd MMM yyyy", Locale.US)
        val title: TextView = itemView.findViewById(R.id.note_title)
        val description: TextView = itemView.findViewById(R.id.note_description)
        val dateChip: Chip = itemView.findViewById(R.id.date_chip)
        fun bind(item: Note, listener: OpenNoteListener?) = with(itemView) {
            title.text = item.title
            description.text = item.text
            if (item.date != null) {
                dateChip.text = formatter.format(item.date)
                dateChip.visibility = View.VISIBLE
            } else {
                dateChip.visibility = View.GONE
            }
            this.setOnClickListener {
                listener?.onNoteClick(item.id, item.title, item.text)
            }
        }
    }

    interface OpenNoteListener {
        fun onNoteClick(noteId: Long?, title: String?, description: String)
    }
}