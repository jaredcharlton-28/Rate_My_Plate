// com/example/rate_my_plate/ui/review/ReviewsActivity.kt
package com.example.rate_my_plate.ui.review

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rate_my_plate.R
import com.example.rate_my_plate.data.model.Review
import com.example.rate_my_plate.databinding.ActivityReviewsBinding
import com.example.rate_my_plate.vm.ReviewViewModel

class ReviewsActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_RESTAURANT_ID = "restaurantId"
        const val EXTRA_RESTAURANT_NAME = "restaurantName"
    }

    private lateinit var binding: ActivityReviewsBinding
    private lateinit var vm: ReviewViewModel
    private lateinit var adapter: ReviewAdapter

    // Listen for write-review result and refresh on return
    private val writeReviewLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { _ -> vm.loadReviews(restaurantId) }

    private val restaurantId by lazy { intent.getStringExtra(EXTRA_RESTAURANT_ID) ?: "demo_restaurant" }
    private val restaurantName by lazy { intent.getStringExtra(EXTRA_RESTAURANT_NAME) ?: "Restaurant" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Title + enable system "Up" arrow (back)
        title = getString(R.string.title_reviews_for, restaurantName)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        vm = ViewModelProvider(this)[ReviewViewModel::class.java]

        adapter = ReviewAdapter(emptyList())
        binding.rvReviews.layoutManager = LinearLayoutManager(this)
        binding.rvReviews.adapter = adapter

        vm.reviews.observe(this) { list: List<Review> ->
            adapter.update(list)
            binding.emptyView.visibility =
                if (list.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
        }

        binding.fabWrite.setOnClickListener {
            writeReviewLauncher.launch(
                Intent(this, WriteReviewActivity::class.java).apply {
                    putExtra(EXTRA_RESTAURANT_ID, restaurantId)
                }
            )
        }

        vm.loadReviews(restaurantId)
    }

    // Handle the Up button (action bar back)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            finish()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    // Also handle navigateUp for good measure
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
