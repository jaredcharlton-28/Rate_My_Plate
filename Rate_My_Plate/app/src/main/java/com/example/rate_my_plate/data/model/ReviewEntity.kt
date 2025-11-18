package com.ratemyplate.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0L, // local primary key
    val serverId: String? = null, // filled after sync
    val restaurantId: String,
    val userId: String,
    val userName: String?,
    val rating: Float,
    val comment: String,
    val imageLocalPath: String? = null, // local file path or null
    val imageUrl: String? = null, // blob URL (once uploaded)
    val ownerResponse: String? = null,
    val status: SyncStatus = SyncStatus.PENDING // PENDING or SYNCED or FAILED
)

enum class SyncStatus { PENDING, SYNCED, FAILED }
