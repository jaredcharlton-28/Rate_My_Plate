package com.example.rate_my_plate.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ReviewDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: OfflineReview)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviews(list: List<OfflineReview>)

    @Query("SELECT * FROM offline_reviews WHERE businessId = :businessId ORDER BY timestamp DESC")
    suspend fun getReviewsForBusiness(businessId: String): List<OfflineReview>

    @Query("SELECT * FROM offline_reviews WHERE isPendingUpload = 1")
    suspend fun getPendingReviews(): List<OfflineReview>

    @Query("DELETE FROM offline_reviews WHERE id = :id")
    suspend fun deleteReviewById(id: String)

    @Insert
    suspend fun insertAll(reviews: List<OfflineReview>)

}
