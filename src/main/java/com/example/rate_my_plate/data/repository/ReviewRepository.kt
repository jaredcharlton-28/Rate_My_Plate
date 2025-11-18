package com.example.rate_my_plate.data.repository

import android.content.Context
import com.example.rate_my_plate.data.local.AppDatabase
import com.example.rate_my_plate.data.local.OfflineReview
import com.example.rate_my_plate.model.ReviewDto
import com.example.rate_my_plate.network.ApiService
import com.example.rate_my_plate.util.NetworkUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.*

class ReviewRepository(private val api: ApiService, val context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val dao = db.reviewDao()

    // Save a review locally (offline)
    suspend fun saveReviewOffline(
        businessId: String,
        userId: String?,
        rating: Int,
        comment: String,
        imageLocalPath: String?
    ): OfflineReview = withContext(Dispatchers.IO) {
        val review = OfflineReview(
            id = UUID.randomUUID().toString(),
            businessId = businessId,
            userId = userId,
            rating = rating,
            comment = comment,
            imageLocalPath = imageLocalPath,
            imageUrl = null,
            timestamp = System.currentTimeMillis(),
            isPendingUpload = true
        )
        dao.insertReview(review)
        review
    }

    // Upload a single review to API
    suspend fun uploadReviewToServer(review: OfflineReview): Boolean = withContext(Dispatchers.IO) {
        try {
            var uploadedImageUrl: String? = null

            // Upload image first if exists
            if (!review.imageLocalPath.isNullOrEmpty()) {
                val imgFile = File(review.imageLocalPath)
                if (imgFile.exists()) {
                    val part = imgFile.asRequestBody("image/*".toMediaTypeOrNull())
                    // TODO: Implement actual image upload API call if needed
                    // uploadedImageUrl = api.uploadImage(part).imageUrl
                }
            }

            // Build DTO to send
            val dto = mapOf(
                "id" to review.id,
                "businessId" to review.businessId,
                "userId" to review.userId,
                "rating" to review.rating,
                "comment" to review.comment,
                "imageUrl" to uploadedImageUrl
            )

            val response = api.postReview(dto)
            if (response.isSuccessful) {
                dao.deleteReviewById(review.id)
                return@withContext true
            }
            return@withContext false

        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        }
    }

    // Upload all pending reviews
    suspend fun uploadPendingReviews() = withContext(Dispatchers.IO) {
        val pending = dao.getPendingReviews()
        for (r in pending) {
            uploadReviewToServer(r)
        }
    }

    // Get reviews for a business
    suspend fun getReviewsForBusiness(businessId: String): List<OfflineReview> = withContext(Dispatchers.IO) {
        return@withContext try {
            if (NetworkUtil.isOnline(context)) {
                val resp: List<ReviewDto> = api.getReviewsForBusiness(businessId)

                // Map API data to OfflineReview
                val localReviews = resp.map { dto ->
                    OfflineReview(
                        id = dto.id,
                        businessId = dto.businessId,
                        userId = dto.userId,
                        rating = dto.rating,
                        comment = dto.comment,
                        imageLocalPath = null,
                        imageUrl = dto.imageUrl,
                        timestamp = dto.timestamp,
                        isPendingUpload = false
                    )
                }

                // Save remote results locally
                dao.insertAll(localReviews)
                localReviews
            } else {
                dao.getReviewsForBusiness(businessId)
            }
        } catch (e: Exception) {
            dao.getReviewsForBusiness(businessId)
        }
    }
}
