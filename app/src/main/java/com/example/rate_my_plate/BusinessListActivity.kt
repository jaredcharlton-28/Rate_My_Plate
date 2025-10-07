package com.example.rate_my_plate.ui.business

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rate_my_plate.R
import com.example.rate_my_plate.data.model.Business
import com.example.rate_my_plate.ui.review.ReviewsActivity

class BusinessListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BusinessAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_list)

        recyclerView = findViewById(R.id.rvBusinesses)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = BusinessAdapter(getSampleBusinesses()) { business ->
            // Open the reviews screen for this business
            startActivity(
                Intent(this, ReviewsActivity::class.java).apply {
                    putExtra(ReviewsActivity.EXTRA_RESTAURANT_ID, business.id)
                    putExtra(ReviewsActivity.EXTRA_RESTAURANT_NAME, business.name)
                }
            )
        }
        recyclerView.adapter = adapter
    }

    // Replace this with real API data later
    private fun getSampleBusinesses(): MutableList<Business> = mutableListOf(
        Business(
            id = "1",
            name = "Pizza Place",
            category = "Restaurant",
            rating = 4.5f,
            thumbnailUrl = null,
            description = "Classic slices & pies"
        ),
        Business(
            id = "2",
            name = "Burger Joint",
            category = "Fast Food",
            rating = 4.0f,
            thumbnailUrl = null,
            description = "Smash burgers and fries"
        ),
        Business(
            id = "3",
            name = "Coffee Corner",
            category = "Cafe",
            rating = 4.8f,
            thumbnailUrl = null,
            description = "Specialty brews & pastries"
        )
    )
}
