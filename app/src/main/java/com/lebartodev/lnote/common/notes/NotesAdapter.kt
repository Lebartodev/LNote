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


class NotesAdapter(private val listener: (note: Note) -> Unit) : RecyclerView.Adapter<NotesAdapter.ViewHolder>() {
    var data: List<Note> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    init {
        setHasStableIds(true)
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
        // val backgroundView: View = itemView.findViewById(R.id.note_creation_background)
        val description: TextView = itemView.findViewById(com.lebartodev.lnote.R.id.note_description)
        val dateChip: Chip = itemView.findViewById(com.lebartodev.lnote.R.id.date_chip)
        fun bind(item: Note) = with(itemView) {
            title.text = item.title
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
        private const val ANIMATION_DURATION = 100L
    }
}