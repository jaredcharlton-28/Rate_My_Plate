package com.example.rate_my_plate.network



import com.example.rate_my_plate.model.Business
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("businesses")
    suspend fun getBusinesses(
        @Query("q") q: String? = null,
        @Query("category") category: String? = null,
        @Query("rating") rating: Float? = null
    ): List<Business>
}
