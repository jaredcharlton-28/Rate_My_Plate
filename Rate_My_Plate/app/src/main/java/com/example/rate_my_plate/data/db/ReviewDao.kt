package com.ratemyplate.data.db

import androidx.room.*
import com.ratemyplate.data.model.ReviewEntity

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews WHERE restaurantId = :restaurantId ORDER BY localId DESC")
    suspend fun getReviewsForRestaurant(restaurantId: String): List<ReviewEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(review: ReviewEntity): Long

    @Update
    suspend fun update(review: ReviewEntity)

    @Delete
    suspend fun delete(review: ReviewEntity)

    @Query("SELECT * FROM reviews WHERE status = :status")
    suspend fun getReviewsByStatus(status: String): List<ReviewEntity>
}
