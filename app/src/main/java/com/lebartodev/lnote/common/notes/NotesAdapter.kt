package com.lebartodev.lnote.common.notes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lebartodev.core.db.entity.Note
import com.lebartodev.lnote.R
import com.lebartodev.lnote.utils.ui.DateChip


class NotesAdapter(private val listener: (note: Note, sharedViews: List<View>) -> Unit) : RecyclerView.Adapter<NotesAdapter.ViewHolder>() {
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
        private val title: TextView = itemView.findViewById(R.id.note_title)
        private val description: TextView = itemView.findViewById(R.id.note_description)
        private val dateChip: DateChip = itemView.findViewById(R.id.note_date_chip)
        private val noteContent: View = itemView.findViewById(R.id.note_item_content)
        fun bind(item: Note, listener: (note: Note, sharedViews: List<View>) -> Unit) {
            noteContent.transitionName = itemView.resources.getString(R.string.note_container_transition_name, item.id.toString())
            title.transitionName = itemView.resources.getString(R.string.note_title_transition_name, item.id.toString())
            description.transitionName = itemView.resources.getString(R.string.note_description_transition_name, item.id.toString())
            dateChip.transitionName = itemView.resources.getString(R.string.note_date_transition_name, item.id.toString())

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

            dateChip.setDate(item.date)

            itemView.setOnClickListener {
                listener(item, listOf(noteContent, title, description, dateChip))
            }
        }
    }

    companion object {
        private const val MAX_LINES = 6
    }
}