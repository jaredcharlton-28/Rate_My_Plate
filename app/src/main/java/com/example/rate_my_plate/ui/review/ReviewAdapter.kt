package com.ratemyplate.ui.review

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ratemyplate.R
import com.ratemyplate.data.model.Review

class ReviewAdapter(private var items: List<Review>) :
    RecyclerView.Adapter<ReviewAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val ivReviewImage: ImageView = view.findViewById(R.id.ivReviewImage)
        val tvRating: TextView = view.findViewById(R.id.tvRating)
        val tvComment: TextView = view.findViewById(R.id.tvComment)
        val tvOwnerResponse: TextView = view.findViewById(R.id.tvOwnerResponse)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_review, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val r = items[position]
        if (!r.imageUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView).load(r.imageUrl).into(holder.ivReviewImage)
        } else {
            holder.ivReviewImage.setImageResource(R.drawable.ic_image_placeholder)
        }

        holder.tvRating.text = "★ ${r.rating}"
        holder.tvComment.text = r.comment
        if (!r.ownerResponse.isNullOrEmpty()) {
            holder.tvOwnerResponse.visibility = View.VISIBLE
            holder.tvOwnerResponse.text = "Owner: ${r.ownerResponse}"
        } else {
            holder.tvOwnerResponse.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = items.size

    fun update(newItems: List<Review>) {
        items = newItems
        notifyDataSetChanged()
        }
}