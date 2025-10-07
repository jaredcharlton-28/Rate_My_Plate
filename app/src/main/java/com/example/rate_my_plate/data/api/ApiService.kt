package com.example.rate_my_plate.data.api

import com.example.rate_my_plate.data.model.Business
import com.example.rate_my_plate.data.model.Review
import retrofit2.Response
import retrofit2.http.*

// Request bodies
data class BusinessCreateRequest(
    val name: String,
    val category: String,
    val description: String? = null,
    val thumbnailUrl: String? = null
)

data class OwnerResponseRequest(val ownerResponse: String)

interface ApiService {

    // ---- Reviews ----
    @POST("reviews")
    suspend fun postReview(@Body review: Review): Response<Review>

    @GET("reviews")
    suspend fun getReviews(@Query("restaurantId") restaurantId: String): Response<List<Review>>

    @PATCH("reviews/{id}")
    suspend fun respondToReview(
        @Path("id") reviewId: String,
        @Body body: OwnerResponseRequest
    ): Response<Review>

    // ---- Businesses ----
    @GET("businesses")
    suspend fun getBusinesses(
        @Query("q") q: String? = null,
        @Query("category") category: String? = null,
        @Query("rating") rating: Float? = null
    ): Response<List<Business>>

    /** Primary path */
    @POST("businesses")
    suspend fun createBusiness(@Body body: BusinessCreateRequest): Response<Business>

    /** Fallback for servers mounted at /api */
    @POST("api/businesses")
    suspend fun createBusinessApi(@Body body: BusinessCreateRequest): Response<Business>
}
