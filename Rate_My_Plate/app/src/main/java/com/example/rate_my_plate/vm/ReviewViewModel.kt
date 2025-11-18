package com.ratemyplate.vm

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ratemyplate.data.model.Review
import com.ratemyplate.data.repository.ReviewRepository
import kotlinx.coroutines.launch

class ReviewViewModel : ViewModel() {

    private val repo = ReviewRepository()

    val postResult = MutableLiveData<Result<Review>>()
    val reviews = MutableLiveData<List<Review>>()

    // ⬅️ New LiveData for offline saved reviews
    val offlineSaveResult = MutableLiveData<Boolean>()


    /**
     * ------------------------------------------------------------
     *  1️⃣ ONLINE SUBMISSION WITH IMAGE UPLOAD
     * ------------------------------------------------------------
     * Called when user is online and submitting a review
     */
    fun submitReviewOnline(review: Review, imageUri: Uri?) {
        viewModelScope.launch {
            try {
                // STEP 1: Upload image if selected
                val finalImageUrl = if (imageUri != null) {
                    repo.uploadImageToBlob(imageUri)
                } else null

                // STEP 2: Update review object
                val updatedReview = review.copy(imageUrl = finalImageUrl)

                // STEP 3: Post review to API
                val resp = repo.postReview(updatedReview)

                if (resp.isSuccessful && resp.body() != null) {
                    postResult.postValue(Result.success(resp.body()!!))
                } else {
                    postResult.postValue(Result.failure(Exception("API Error: ${resp.code()}")))
                }

            } catch (e: Exception) {
                postResult.postValue(Result.failure(e))
            }
        }
    }


    /**
     * ------------------------------------------------------------
     *  2️⃣ OFFLINE: SAVE REVIEW LOCALLY
     * ------------------------------------------------------------
     * Called when no internet – saves to RoomDB as pending review
     */
    fun saveReviewOffline(review: Review) {
        viewModelScope.launch {
            try {
                repo.saveReviewOffline(review)
                offlineSaveResult.postValue(true)
            } catch (e: Exception) {
                offlineSaveResult.postValue(false)
            }
        }
    }


    /**
     * ------------------------------------------------------------
     *  3️⃣ LOAD REVIEWS (online)
     * ------------------------------------------------------------
     */
    fun loadReviews(restaurantId: String) {
        viewModelScope.launch {
            try {
                val resp = repo.getReviews(restaurantId)
                if (resp.isSuccessful) {
                    reviews.postValue(resp.body() ?: emptyList())
                } else {
                    reviews.postValue(emptyList())
                }
            } catch (e: Exception) {
                reviews.postValue(emptyList())
            }
        }
    }


    /**
     * ------------------------------------------------------------
     *  4️⃣ SYNC PENDING OFFLINE REVIEWS
     * ------------------------------------------------------------
     * Called by WorkManager, NOT by the UI
     */
    suspend fun syncPendingReviews() {
        repo.syncPendingReviews()
    }
}
