package com.example.rate_my_plate.ui.review

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rate_my_plate.LocaleManager
import com.example.rate_my_plate.R
import com.example.rate_my_plate.data.model.Review
import com.example.rate_my_plate.databinding.ActivityReviewsBinding
import com.example.rate_my_plate.notifications.ReviewMessagingService
import com.example.rate_my_plate.vm.ReviewViewModel
import com.google.android.material.snackbar.Snackbar

class ReviewsActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_RESTAURANT_ID = "restaurantId"
        const val EXTRA_RESTAURANT_NAME = "restaurantName"
    }

    private lateinit var binding: ActivityReviewsBinding
    private lateinit var vm: ReviewViewModel
    private lateinit var adapter: ReviewAdapter
    private lateinit var broadcastManager: LocalBroadcastManager
    private val notificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val businessId = intent?.getStringExtra(ReviewMessagingService.EXTRA_BUSINESS_ID)
            if (businessId == restaurantId) {
                vm.loadReviews(restaurantId)
                Snackbar.make(
                    binding.root,
                    R.string.notification_new_review_body,
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    // Listen for write-review result and refresh on return
    private val writeReviewLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { _ -> vm.loadReviews(restaurantId) }

    private val restaurantId by lazy { intent.getStringExtra(EXTRA_RESTAURANT_ID) ?: "demo_restaurant" }
    private val restaurantName by lazy { intent.getStringExtra(EXTRA_RESTAURANT_NAME) ?: "Restaurant" }

    // Apply saved language to this Activity
    override fun attachBaseContext(newBase: Context) {
        val lang = LocaleManager.getSavedLanguage(newBase)
        val wrapped = LocaleManager.setLocale(newBase, lang)
        super.attachBaseContext(wrapped)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Title + enable system "Up" arrow (back)
        title = getString(R.string.title_reviews_for, restaurantName)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        vm = ViewModelProvider(this)[ReviewViewModel::class.java]
        broadcastManager = LocalBroadcastManager.getInstance(this)

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

    override fun onStart() {
        super.onStart()
        broadcastManager.registerReceiver(
            notificationReceiver,
            IntentFilter(ReviewMessagingService.ACTION_REVIEW_UPDATE)
        )
    }

    override fun onStop() {
        super.onStop()
        broadcastManager.unregisterReceiver(notificationReceiver)
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
