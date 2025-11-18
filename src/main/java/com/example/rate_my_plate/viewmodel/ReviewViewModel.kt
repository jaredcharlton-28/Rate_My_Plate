package com.example.rate_my_plate.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rate_my_plate.data.repository.ReviewRepository
import com.example.rate_my_plate.data.local.OfflineReview
import com.example.rate_my_plate.util.NetworkUtil
import kotlinx.coroutines.launch
import android.content.Context


class ReviewViewModel(private val repo: ReviewRepository) : ViewModel() {

    val uploadState = MutableLiveData<Boolean>()
    val reviewsLive = MutableLiveData<List<OfflineReview>>()

    // ✅ Replace the old submitReview with this version
    fun submitReview(
        context: Context,
        businessId: String,
        userId: String?,
        rating: Int,
        comment: String,
        imageLocalPath: String?
    ) {
        viewModelScope.launch {
            if (NetworkUtil.isOnline(context)) {
                val tempReview = repo.saveReviewOffline(businessId, userId, rating, comment, imageLocalPath)
                val ok = repo.uploadReviewToServer(tempReview)
                uploadState.postValue(ok)
            } else {
                repo.saveReviewOffline(businessId, userId, rating, comment, imageLocalPath)
                uploadState.postValue(false)
            }
        }
    }

    fun loadReviews(businessId: String) {
        viewModelScope.launch {
            val list = repo.getReviewsForBusiness(businessId)
            reviewsLive.postValue(list)
        }
    }
}
