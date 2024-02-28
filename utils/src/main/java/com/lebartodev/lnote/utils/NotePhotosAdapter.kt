package com.lebartodev.lnote.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class NotePhotosAdapter : RecyclerView.Adapter<NotePhotosAdapter.ViewHolder>() {
    private val data: MutableList<String> = mutableListOf()

    init {
        setHasStableIds(true)
    }

    fun updateData(list: List<String>) {
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return data[position].hashCode().toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.i_note_photo, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(data[position])

    override fun getItemCount() = data.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image: ImageView = itemView.findViewById(R.id.image)
        fun bind(item: String) {
            Glide.with(image.context).load(item).into(image)
        }
    }
}