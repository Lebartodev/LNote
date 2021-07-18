package com.lebartodev.lnote.feature_attach

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class PhotosAdapter(private val listener: (Photo) -> Unit) :
    RecyclerView.Adapter<PhotosAdapter.ViewHolder>() {
    val data: MutableList<Photo> = mutableListOf()

    init {
        setHasStableIds(true)
    }

    fun updateData(notes: List<Photo>) {
        data.clear()
        data.addAll(notes)
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return data[position].path.hashCode().toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.i_photo, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(data[position],
        listener)

    override fun getItemCount() = data.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image: ImageView = itemView.findViewById(R.id.image)
        fun bind(item: Photo, listener: (Photo) -> Unit) {
            Glide.with(image.context).load(item.path).into(image)
            itemView.setOnClickListener {
                listener(item)
            }
        }
    }
}