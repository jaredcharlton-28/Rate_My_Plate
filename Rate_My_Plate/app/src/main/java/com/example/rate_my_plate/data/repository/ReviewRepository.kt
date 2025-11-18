package com.ratemyplate.data.repository

import android.net.Uri
import com.ratemyplate.data.db.AppDatabase
import com.ratemyplate.data.model.Review
import com.ratemyplate.data.model.ReviewEntity
import com.ratemyplate.data.network.ApiClient
import com.ratemyplate.data.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReviewRepository {

    private val api: ApiService = ApiClient.apiService
    private val db = AppDatabase.instance

    /**
     * ------------------------------------------------------------
     *  1️⃣ POST REVIEW ONLINE (API)
     * ------------------------------------------------------------
     */
    suspend fun postReview(review: Review) =
        api.postReview(review)


    /**
     * ------------------------------------------------------------
     *  2️⃣ GET REVIEWS FROM API
     * ------------------------------------------------------------
     */
    suspend fun getReviews(restaurantId: String) =
        api.getReviews(restaurantId)


    /**
     * ------------------------------------------------------------
     *  3️⃣ IMAGE UPLOAD PLACEHOLDER
     * ------------------------------------------------------------
     * Replace with Firebase Storage when your teammates finish.
     */
    suspend fun uploadImageToBlob(uri: Uri): String? {
        // Placeholder until Firebase Storage is set up by the group
        // This allows your code to compile and run.
        return null
    }


    /**
     * ------------------------------------------------------------
     *  4️⃣ SAVE REVIEW LOCALLY (OFFLINE)
     * ------------------------------------------------------------
     */
    suspend fun saveReviewOffline(review: Review) {
        withContext(Dispatchers.IO) {
            db.reviewDao().insert(
                ReviewEntity(
                    restaurantId = review.restaurantId,
                    userId = review.userId,
                    userName = review.userName,
                    rating = review.rating,
                    comment = review.comment,
                    imageUrl = review.imageUrl,
                    isPending = true
                )
            )
        }
    }


    /**
     * ------------------------------------------------------------
     *  5️⃣ GET ALL PENDING (UNSYNCED) REVIEWS
     * ------------------------------------------------------------
     */
    suspend fun getPendingReviews(): List<ReviewEntity> {
        return withContext(Dispatchers.IO) {
            db.reviewDao().getPendingReviews()
        }
    }


    /**
     * ------------------------------------------------------------
     *  6️⃣ SYNC OFFLINE REVIEWS
     * ------------------------------------------------------------
     */
    suspend fun syncPendingReviews() {
        val pending = getPendingReviews()

        pending.forEach { entity ->
            val review = Review(
                restaurantId = entity.restaurantId,
                userId = entity.userId,
                userName = entity.userName,
                rating = entity.rating,
                comment = entity.comment,
                imageUrl = entity.imageUrl,
                isPending = false
            )

            try {
                val resp = api.postReview(review)

                if (resp.isSuccessful) {
                    deletePendingReview(entity)
                }
            } catch (_: Exception) { }
        }
    }


    /**
     * ------------------------------------------------------------
     *  7️⃣ DELETE AFTER SYNC
     * ------------------------------------------------------------
     */
    private suspend fun deletePendingReview(entity: ReviewEntity) {
        withContext(Dispatchers.IO) {
            db.reviewDao().delete(entity)
        }
    }
}
