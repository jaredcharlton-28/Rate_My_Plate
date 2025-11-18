package com.ratemyplate

import com.ratemyplate.data.model.ReviewEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class ReviewEntityTest {
    @Test
    fun entityToString_checkComment() {
        val e = ReviewEntity(
            restaurantId = "r1",
            userId = "u1",
            userName = "Teshar",
            rating = 4.5f,
            comment = "Good"
        )
        assertEquals("Good", e.comment)
    }
}
