package com.example.rate_my_plate.data.repository

import android.util.Log
import com.example.rate_my_plate.RateMyPlateApp
import com.example.rate_my_plate.data.api.ApiClient
import com.example.rate_my_plate.data.api.BusinessCreateRequest
import com.example.rate_my_plate.data.local.AppDatabase
import com.example.rate_my_plate.data.local.BusinessEntity
import com.example.rate_my_plate.data.model.Business
import retrofit2.Response

data class CachedBusinesses(
    val businesses: List<Business>,
    val fromCache: Boolean
)

class BusinessRepository(
    private val db: AppDatabase = AppDatabase.get(RateMyPlateApp.instance)
) {

    private val service = ApiClient.service

    suspend fun getBusinesses(
        q: String? = null,
        category: String? = null,
        rating: Float? = null
    ): Response<List<Business>> = service.getBusinesses(q, category, rating)

    suspend fun getBusinessesWithCache(): CachedBusinesses {
        return try {
            val resp = getBusinesses()
            if (resp.isSuccessful) {
                val network = resp.body().orEmpty()
                db.businessDao().clearAll()
                db.businessDao().insertAll(network.map(BusinessEntity::fromBusiness))
                CachedBusinesses(network, fromCache = false)
            } else {
                val cached = db.businessDao().getAll().map { it.toBusiness() }
                CachedBusinesses(cached, fromCache = true)
            }
        } catch (_: Exception) {
            val cached = db.businessDao().getAll().map { it.toBusiness() }
            CachedBusinesses(cached, fromCache = true)
        }
    }

    suspend fun createBusiness(
        name: String,
        category: String,
        description: String?,
        thumbnailUrl: String?
    ): Response<Business> {
        val body = BusinessCreateRequest(name, category, description, thumbnailUrl)

        val primaryUrl = ApiClient.retrofit.baseUrl().toString() + "businesses"
        Log.d("BusinessRepository", "POST -> $primaryUrl")

        val r1 = service.createBusiness(body)
        if (r1.isSuccessful || r1.code() != 404) {
            // success or some other error (400/401/etc) — return it
            if (!r1.isSuccessful) {
                Log.e("BusinessRepository", "Primary failed: code=${r1.code()} msg=${r1.message()}")
            }
            return r1
        }

        // 404: try the /api/businesses fallback
        val fallbackUrl = ApiClient.retrofit.baseUrl().toString() + "api/businesses"
        Log.w("BusinessRepository", "Primary 404. Retrying -> $fallbackUrl")

        val r2 = service.createBusinessApi(body)
        if (!r2.isSuccessful) {
            Log.e("BusinessRepository", "Fallback failed: code=${r2.code()} msg=${r2.message()}")
        }
        return r2
    }
}
