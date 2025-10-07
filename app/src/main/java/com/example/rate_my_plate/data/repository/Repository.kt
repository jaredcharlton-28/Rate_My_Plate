package com.example.rate_my_plate.data.repository

import com.example.rate_my_plate.data.api.ApiClient
import com.example.rate_my_plate.data.api.OwnerResponseRequest
import com.example.rate_my_plate.data.model.Review
import retrofit2.Response

class ReviewRepository {
    private val service = ApiClient.service

    suspend fun postReview(review: Review): Response<Review> =
        service.postReview(review)

    suspend fun getReviews(restaurantId: String): Response<List<Review>> =
        service.getReviews(restaurantId)

    suspend fun respondToReview(reviewId: String, text: String): Response<Review> =
        service.respondToReview(reviewId, OwnerResponseRequest(text))
}
