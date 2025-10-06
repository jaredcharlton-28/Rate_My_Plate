package com.ratemyplate.data.model

data class Review(
    val id: String? = null,
    val restaurantId: String,
    val userId: String,
    val userName: String? = null,
    val rating: Float,
    val comment: String,
    val imageUrl: String? = null,
    val ownerResponse: String=null
)