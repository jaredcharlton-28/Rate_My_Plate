package com.example.rate_my_plate.network

import com.example.rate_my_plate.model.ReviewDto
import com.example.rate_my_plate.model.Business
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @GET("businesses")
    suspend fun getBusinesses(): List<Business>

    @POST("reviews")
    suspend fun postReview(@Body review: Map<String, Any?>): Response<Void>

    @GET("businesses/{id}/reviews")
    suspend fun getReviewsForBusiness(@Path("id") businessId: String): List<ReviewDto>
}
