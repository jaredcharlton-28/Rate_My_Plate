package com.example.rate_my_plate.model

data class Business(
    val id: String,
    val name: String,
    val category: Float,
    val rating: String,
    val thumbnailUrl: String?,
    val description: String? = null
)
