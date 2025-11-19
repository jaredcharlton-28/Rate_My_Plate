package com.example.rate_my_plate.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rate_my_plate.data.model.Business

@Entity(tableName = "businesses")
data class BusinessEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val rating: Float,
    val thumbnailUrl: String?,
    val description: String?,
    val ownerId: String?
) {
    fun toBusiness(): Business = Business(
        id = id,
        name = name,
        category = category,
        rating = rating,
        thumbnailUrl = thumbnailUrl,
        description = description,
        ownerId = ownerId
    )

    companion object {
        fun fromBusiness(business: Business) = BusinessEntity(
            id = business.id,
            name = business.name,
            category = business.category,
            rating = business.rating,
            thumbnailUrl = business.thumbnailUrl,
            description = business.description,
            ownerId = business.ownerId
        )
    }
}
