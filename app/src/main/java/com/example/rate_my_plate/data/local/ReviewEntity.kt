package com.example.rate_my_plate.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rate_my_plate.data.model.Review

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0,
    val remoteId: String?,           // API id (nullable if not uploaded yet)
    val restaurantId: String,
    val userId: String,
    val userName: String?,
    val rating: Float,
    val comment: String,
    val imageUrl: String?,
    val ownerResponse: String?,
    val pendingUpload: Boolean,      // true if saved while offline
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toReview(): Review = Review(
        id = remoteId,
        restaurantId = restaurantId,
        userId = userId,
        userName = userName,
        rating = rating,
        comment = comment,
        imageUrl = imageUrl,
        ownerResponse = ownerResponse
    )

    companion object {
        fun fromReview(review: Review, pending: Boolean): ReviewEntity =
            ReviewEntity(
                remoteId = review.id,
                restaurantId = review.restaurantId,
                userId = review.userId,
                userName = review.userName,
                rating = review.rating,
                comment = review.comment,
                imageUrl = review.imageUrl,
                ownerResponse = review.ownerResponse,
                pendingUpload = pending
            )
    }
}
