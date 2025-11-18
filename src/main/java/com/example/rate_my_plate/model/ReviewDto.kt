package com.example.rate_my_plate.model

data class ReviewDto(
    val id: String,
    val businessId: String,
    val userId: String?,
    val rating: Int,
    val comment: String,
    val imageUrl: String?,
    val timestamp: Long
)
