package com.example.rate_my_plate.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rate_my_plate.data.model.Review
import com.example.rate_my_plate.data.repository.ReviewRepository
import kotlinx.coroutines.launch

class ReviewViewModel : ViewModel() {
    private val repo = ReviewRepository()

    val postResult = MutableLiveData<Result<Review>>()
    val reviews = MutableLiveData<List<Review>>()
    val respondResult = MutableLiveData<Result<Unit>>()  // owner reply result

    fun postReview(review: Review) {
        viewModelScope.launch {
            try {
                val resp = repo.postReview(review)
                if (resp.isSuccessful) {
                    val body = resp.body()
                    if (body != null) postResult.postValue(Result.success(body))
                    else postResult.postValue(Result.failure(IllegalStateException("Empty body")))
                } else {
                    postResult.postValue(Result.failure(Exception("${resp.code()} ${resp.message()}")))
                }
            } catch (e: Exception) {
                postResult.postValue(Result.failure(e))
            }
        }
    }

    fun loadReviews(restaurantId: String) {
        viewModelScope.launch {
            try {
                val resp = repo.getReviews(restaurantId)
                if (resp.isSuccessful) {
                    reviews.postValue(resp.body().orEmpty())
                } else {
                    reviews.postValue(emptyList())
                }
            } catch (_: Exception) {
                reviews.postValue(emptyList())
            }
        }
    }

    // NEW: owner responds to a review
    fun respondToReview(reviewId: String, text: String) {
        viewModelScope.launch {
            try {
                val resp = repo.respondToReview(reviewId, text)
                if (resp.isSuccessful) {
                    respondResult.postValue(Result.success(Unit))
                } else {
                    respondResult.postValue(Result.failure(Exception("${resp.code()} ${resp.message()}")))
                }
            } catch (e: Exception) {
                respondResult.postValue(Result.failure(e))
            }
        }
    }
}
