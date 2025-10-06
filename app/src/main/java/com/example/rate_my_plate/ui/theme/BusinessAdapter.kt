package com.example.rate_my_plate.ui.theme



import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rate_my_plate.R
import com.example.rate_my_plate.model.Business
import de.hdodenhof.circleimageview.CircleImageView

class BusinessAdapter(
    private var items: MutableList<Business> = mutableListOf(),
    private val onClick: (Business) -> Unit
) : RecyclerView.Adapter<BusinessAdapter.BusinessVH>() {

    inner class BusinessVH(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvBusinessName)
        val category: TextView = view.findViewById(R.id.tvBusinessCategory)
        val ratingBar: RatingBar = view.findViewById(R.id.rbBusinessRating)
        val thumbnail: CircleImageView = view.findViewById(R.id.ivThumbnail)
        init {
            view.setOnClickListener {
                onClick(items[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusinessVH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_business, parent, false)
        return BusinessVH(view)
    }

    override fun onBindViewHolder(holder: BusinessVH, position: Int) {
        val b = items[position]
        holder.name.text = b.name
        holder.category.text = b.category
        holder.ratingBar.rating = b.rating

        Glide.with(holder.thumbnail.context)
            .load(b.thumbnailUrl)
            .placeholder(R.drawable.ic_placeholder)
            .into(holder.thumbnail)
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<Business>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}

private fun ERROR.into(thumbnail: CircleImageView) {}
