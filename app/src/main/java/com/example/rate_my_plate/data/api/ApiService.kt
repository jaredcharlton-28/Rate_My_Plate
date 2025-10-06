package com.ratemyplate.data.api

import com.ratemyplate.data.model.Review
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("reviews")
    suspend fun postReview(@Body review: Review): Response<Review>

    @GET("reviews")
    suspend fun getReviews(@Query("restaurantId") restaurantId: String): Response<List<Review>>
}