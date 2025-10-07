package com.example.rate_my_plate.ui.business

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rate_my_plate.R
import com.example.rate_my_plate.data.model.Business
import de.hdodenhof.circleimageview.CircleImageView

class BusinessAdapter(
    private val items: MutableList<Business> = mutableListOf(),
    private val onClick: (Business) -> Unit
) : RecyclerView.Adapter<BusinessAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val thumbnail: CircleImageView = view.findViewById(R.id.ivThumbnail)
        val name: TextView = view.findViewById(R.id.tvBusinessName)
        val category: TextView = view.findViewById(R.id.tvBusinessCategory)
        val ratingBar: RatingBar = view.findViewById(R.id.rbBusinessRating)
        init {
            view.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) onClick(items[pos])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_business, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val b = items[position]
        holder.name.text = b.name
        holder.category.text = b.category
        holder.ratingBar.rating = b.rating

        // —— robust thumbnail handling ——
        if (b.thumbnailUrl.isNullOrBlank()) {
            // No URL -> set a bitmap-backed placeholder directly
            holder.thumbnail.setImageResource(android.R.drawable.ic_menu_report_image)
        } else {
            Glide.with(holder.thumbnail)
                .load(b.thumbnailUrl)
                .placeholder(android.R.drawable.ic_menu_report_image)
                .error(android.R.drawable.ic_menu_report_image)
                .fallback(android.R.drawable.ic_menu_report_image)
                .into(holder.thumbnail)
        }
    }

    override fun getItemCount(): Int = items.size

    fun submit(list: List<Business>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }
}
