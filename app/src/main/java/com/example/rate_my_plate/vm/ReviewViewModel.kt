package com.example.rate_my_plate.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rate_my_plate.RateMyPlateApp
import com.example.rate_my_plate.data.local.AppDatabase
import com.example.rate_my_plate.data.local.ReviewEntity
import com.example.rate_my_plate.data.model.Review
import com.example.rate_my_plate.data.repository.ReviewRepository
import kotlinx.coroutines.launch

class ReviewViewModel : ViewModel() {

    private val repo = ReviewRepository()
    private val reviewDao = AppDatabase.get(RateMyPlateApp.instance).reviewDao()

    val postResult = MutableLiveData<Result<Review>>()
    val reviews = MutableLiveData<List<Review>>()
    val respondResult = MutableLiveData<Result<Unit>>()  // owner reply result

    /**
     * Load reviews for a restaurant.
     * - Try API first
     * - On failure / offline: fall back to local Room cache
     */
    fun loadReviews(restaurantId: String) {
        viewModelScope.launch {
            try {
                val resp = repo.getReviews(restaurantId)
                if (resp.isSuccessful) {
                    val remoteList = resp.body().orEmpty()

                    // refresh local cache with latest non-pending reviews
                    reviewDao.clearNonPendingForRestaurant(restaurantId)
                    reviewDao.insertAll(
                        remoteList.map { ReviewEntity.fromReview(it, pending = false) }
                    )

                    reviews.postValue(remoteList)
                } else {
                    // API error → use local cache
                    val local = reviewDao.getReviewsForRestaurant(restaurantId)
                    reviews.postValue(local.map { it.toReview() })
                }
            } catch (e: Exception) {
                // Exception (likely offline) → use local cache
                val local = reviewDao.getReviewsForRestaurant(restaurantId)
                reviews.postValue(local.map { it.toReview() })
            }
        }
    }

    /**
     * Post a review.
     * - On success: send to API and cache it locally as non-pending
     * - On failure / offline: save it locally with pendingUpload = true
     */
    fun postReview(review: Review) {
        viewModelScope.launch {
            try {
                val resp = repo.postReview(review)
                if (resp.isSuccessful) {
                    val body = resp.body()!!

                    // save successful review in local cache
                    reviewDao.insert(
                        ReviewEntity.fromReview(body, pending = false)
                    )

                    postResult.postValue(Result.success(body))
                } else {
                    // API error → keep review offline as pending upload
                    reviewDao.insert(
                        ReviewEntity.fromReview(review, pending = true)
                    )
                    postResult.postValue(
                        Result.failure(
                            Exception("Saved offline (server error ${resp.code()})")
                        )
                    )
                }
            } catch (e: Exception) {
                // Network exception → offline cache
                reviewDao.insert(
                    ReviewEntity.fromReview(review, pending = true)
                )
                postResult.postValue(
                    Result.failure(
                        Exception("Saved offline (no connection)")
                    )
                )
            }
        }
    }

    /**
     * Owner respond to a review via the API.
     * (Pure network, no caching right now)
     */
    fun respondToReview(reviewId: String, text: String) {
        viewModelScope.launch {
            try {
                val resp = repo.respondToReview(reviewId, text)
                if (resp.isSuccessful) {
                    respondResult.postValue(Result.success(Unit))
                } else {
                    respondResult.postValue(
                        Result.failure(
                            Exception("${resp.code()} ${resp.message()}")
                        )
                    )
                }
            } catch (e: Exception) {
                respondResult.postValue(Result.failure(e))
            }
        }
    }
}
