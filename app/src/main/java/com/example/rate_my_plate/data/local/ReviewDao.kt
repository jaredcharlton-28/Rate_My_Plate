package com.example.rate_my_plate.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ReviewDao {

    @Query("SELECT * FROM reviews WHERE restaurantId = :restaurantId ORDER BY createdAt DESC")
    suspend fun getReviewsForRestaurant(restaurantId: String): List<ReviewEntity>

    @Query("DELETE FROM reviews WHERE restaurantId = :restaurantId AND pendingUpload = 0")
    suspend fun clearNonPendingForRestaurant(restaurantId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(review: ReviewEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reviews: List<ReviewEntity>)

    @Query("SELECT * FROM reviews WHERE pendingUpload = 1")
    suspend fun getPendingUploads(): List<ReviewEntity>

    @Query("DELETE FROM reviews WHERE localId = :localId")
    suspend fun deleteById(localId: Long)
}
