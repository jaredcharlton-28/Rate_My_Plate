package com.example.rate_my_plate.ui.review

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rate_my_plate.R
import com.example.rate_my_plate.data.model.Review

/**
 * Review list adapter.
 *
 * @param onOwnerReplyRequested optional callback fired on long-press of a review
 *                              (use it when the current user is the restaurant owner).
 * @param canOwnerReply if true, enables the long-press gesture; otherwise disabled.
 */
class ReviewAdapter(
    private var items: List<Review>,
    private val onOwnerReplyRequested: ((Review) -> Unit)? = null,
    private var canOwnerReply: Boolean = false
) : RecyclerView.Adapter<ReviewAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val ivReviewImage: ImageView = view.findViewById(R.id.ivReviewImage)
        val tvRating: TextView = view.findViewById(R.id.tvRating)
        val tvComment: TextView = view.findViewById(R.id.tvComment)
        val tvOwnerResponse: TextView = view.findViewById(R.id.tvOwnerResponse)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_review, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val r = items[position]

        // Image
        if (!r.imageUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView).load(r.imageUrl).into(holder.ivReviewImage)
        } else {
            holder.ivReviewImage.setImageResource(android.R.drawable.ic_menu_report_image)
        }

        // Text fields
        holder.tvRating.text = "★ ${r.rating}"
        holder.tvComment.text = r.comment

        // Owner response
        if (!r.ownerResponse.isNullOrEmpty()) {
            holder.tvOwnerResponse.visibility = View.VISIBLE
            holder.tvOwnerResponse.text = "Owner: ${r.ownerResponse}"
        } else {
            holder.tvOwnerResponse.visibility = View.GONE
        }

        // Long-press to add/edit owner response (only when allowed)
        if (canOwnerReply && onOwnerReplyRequested != null) {
            holder.itemView.setOnLongClickListener {
                onOwnerReplyRequested.invoke(r)
                true
            }
        } else {
            holder.itemView.setOnLongClickListener(null)
        }
    }

    override fun getItemCount(): Int = items.size

    /** Replace the list and refresh. */
    fun update(newItems: List<Review>) {
        items = newItems
        notifyDataSetChanged()
    }

    /** Enable/disable owner reply behavior dynamically (optional). */
    fun setCanOwnerReply(enabled: Boolean) {
        canOwnerReply = enabled
        notifyDataSetChanged()
    }
}
