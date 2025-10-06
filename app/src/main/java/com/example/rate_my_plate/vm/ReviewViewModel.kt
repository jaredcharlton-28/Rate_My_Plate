package com.ratemyplate.vm

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

    fun postReview(review: Review) {
        viewModelScope.launch {
            try {
                val resp = repo.postReview(review)
                if (resp.isSuccessful) {
                    postResult.postValue(Result.success(resp.body()!!))
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
                    reviews.postValue(resp.body())
                } else {
                    reviews.postValue(emptyList())
                }
            } catch (e: Exception) {
                reviews.postValue(emptyList())
            }
            }
        }
}