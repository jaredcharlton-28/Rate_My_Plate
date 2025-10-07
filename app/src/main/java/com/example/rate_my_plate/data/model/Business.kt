package com.example.rate_my_plate.data.model

data class Business(
    val id: String,
    val name: String,
    val category: String,        // <- was Float; should be text
    val rating: Float,           // <- was String; used by RatingBar
    val thumbnailUrl: String? = null,
    val description: String? = null,
    val ownerId: String? = null
)
