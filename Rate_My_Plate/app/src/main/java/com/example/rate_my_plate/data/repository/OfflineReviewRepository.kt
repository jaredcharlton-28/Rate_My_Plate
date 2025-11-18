package com.ratemyplate.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.ratemyplate.data.api.ApiClient
import com.ratemyplate.data.db.AppDatabase
import com.ratemyplate.data.model.Review
import com.ratemyplate.data.model.ReviewEntity
import com.ratemyplate.data.model.SyncStatus
import kotlinx.coroutines.tasks.await
import retrofit2.Response
import java.io.File

class OfflineReviewRepository(private val context: Context) {

    private val db = AppDatabase.getInstance(context)
    private val reviewDao = db.reviewDao()
    private val api = ApiClient.service
    private val storage = Firebase.storage
    private val TAG = "OfflineReviewRepo"

    // Save review locally (imageLocalPath is optional — content URI path or local file path)
    suspend fun saveReviewLocally(entity: ReviewEntity): Long {
        Log.d(TAG, "Saving review locally: ${entity.restaurantId}")
        return reviewDao.insert(entity)
    }

    // Upload image to Firebase Storage and return download URL
    suspend fun uploadImageAndGetUrl(localPath: String): String? {
        return try {
            val fileUri = Uri.parse(localPath) // localPath should be a string URI, e.g. content://...
            val fileName = "reviews/${System.currentTimeMillis()}_${File(fileUri.path ?: "img")}"
            val ref = storage.reference.child(fileName)
            val uploadTask = ref.putFile(fileUri).await()
            val url = ref.downloadUrl.await().toString()
            Log.d(TAG, "Uploaded image URL: $url")
            url
        } catch (e: Exception) {
            Log.e(TAG, "uploadImageAndGetUrl failed", e)
            null
        }
    }

    // Post review to server
    suspend fun postReviewToServer(review: Review): Response<Review> {
        return api.postReview(review)
    }

    // Sync pending reviews: upload images (if any), post to server, update Room
    suspend fun syncPendingReviews() {
        val pending = reviewDao.getReviewsByStatus(SyncStatus.PENDING.name)
        for (entity in pending) {
            try {
                var imageUrl: String? = entity.imageUrl
                // If there is a local image path and no imageUrl, upload it
                if (!entity.imageLocalPath.isNullOrEmpty() && imageUrl.isNullOrEmpty()) {
                    imageUrl = uploadImageAndGetUrl(entity.imageLocalPath)
                }

                // create Review model to send to server
                val reviewToSend = Review(
                    restaurantId = entity.restaurantId,
                    userId = entity.userId,
                    userName = entity.userName,
                    rating = entity.rating,
                    comment = entity.comment,
                    imageUrl = imageUrl,
                    ownerResponse = entity.ownerResponse
                )

                val resp = postReviewToServer(reviewToSend)
                if (resp.isSuccessful) {
                    // mark as synced and store serverId if available
                    val serverId = resp.body()?.id
                    val updated = entity.copy(
                        serverId = serverId,
                        imageUrl = imageUrl,
                        status = SyncStatus.SYNCED
                    )
                    reviewDao.update(updated)
                    Log.d(TAG, "Synced local review ${entity.localId} -> server ${serverId}")
                } else {
                    // mark failed
                    val failed = entity.copy(status = SyncStatus.FAILED)
                    reviewDao.update(failed)
                    Log.e(TAG, "Failed to post review: ${resp.code()} ${resp.message()}")
                }
            } catch (e: Exception) {
                // network error - keep as pending
                val failed = entity.copy(status = SyncStatus.FAILED)
                reviewDao.update(failed)
                Log.e(TAG, "Exception while syncing review ${entity.localId}", e)
            }
        }
    }

    // read reviews for a restaurant from server (fresh)
    suspend fun fetchReviewsFromServer(restaurantId: String): List<Review>? {
        return try {
            val resp = api.getReviews(restaurantId)
            if (resp.isSuccessful) resp.body() else null
        } catch (e: Exception) {
            Log.e(TAG, "fetchReviewsFromServer failed", e)
            null
        }
    }

    // read reviews for a restaurant from local DB
    suspend fun fetchReviewsFromLocal(restaurantId: String): List<ReviewEntity> =
        reviewDao.getReviewsForRestaurant(restaurantId)
}
