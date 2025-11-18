package com.example.rate_my_plate.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "offline_reviews")
data class OfflineReview(
    @PrimaryKey val id: String,            // use UUID for client-side reviews
    val businessId: String,
    val userId: String?,
    val rating: Int,
    val comment: String,
    val imageLocalPath: String?,           // optional local file path (if image saved locally)
    val imageUrl: String?,                 // populated after successful upload
    val timestamp: Long,
    val isPendingUpload: Boolean = true    // true = needs to be uploaded to server
)
