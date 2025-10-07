package com.ratemyplate.data.repository

import com.ratemyplate.data.api.ApiClient
import com.ratemyplate.data.model.Review
import retrofit2.Response

class ReviewRepository {
    private val service = ApiClient.service

    suspend fun postReview(review: Review): Response<Review> {
        return service.postReview(review)
    }

    suspend fun getReviews(restaurantId: String): Response<List<Review>> {
        return service.getReviews(restaurantId)
        }
}