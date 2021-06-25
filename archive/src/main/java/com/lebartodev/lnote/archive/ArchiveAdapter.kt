package com.lebartodev.lnote.archive

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lebartodev.core.db.entity.Note
import com.lebartodev.lnote.utils.ui.DateChip


class ArchiveAdapter(private val deleteListener: (note: Note) -> Unit,
                     private val restoreListener: (note: Note) -> Unit) :
    RecyclerView.Adapter<ArchiveAdapter.ViewHolder>() {
    private var expandedItemId: Long? = null
    private val data: MutableList<Note> = mutableListOf()

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
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.i_note_archive, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(data[position],
        data[position].id == expandedItemId,
        {
            expandedItemId = if (data[position].id == expandedItemId) null else it.id
            notifyItemChanged(position)
        }, deleteListener, restoreListener)

    override fun getItemCount() = data.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.note_title)
        private val description: TextView = itemView.findViewById(R.id.note_description)
        private val dateChip: DateChip = itemView.findViewById(R.id.note_date_chip)
        private val deleteButton: Button = itemView.findViewById(R.id.delete_button)
        private val restoreButton: Button = itemView.findViewById(R.id.restore_button)
        fun bind(item: Note, isViewExpanded: Boolean, listener: (note: Note) -> Unit,
                 deleteListener: (note: Note) -> Unit,
                 restoreListener: (note: Note) -> Unit) {
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
                listener(item)

            }
            deleteButton.visibility = if (isViewExpanded) View.VISIBLE else View.GONE
            restoreButton.visibility = if (isViewExpanded) View.VISIBLE else View.GONE
            deleteButton.setOnClickListener {
                deleteListener(item)
            }
            restoreButton.setOnClickListener {
                restoreListener(item)
            }
        }
    }

    companion object {
        private const val MAX_LINES = 6
    }
}