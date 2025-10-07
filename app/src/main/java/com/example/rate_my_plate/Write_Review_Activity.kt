package com.example.rate_my_plate.ui.review

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.example.rate_my_plate.R

class WriteReviewActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_RESTAURANT_ID = "restaurantId"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_review)

        if (savedInstanceState == null) {
            val rid = intent.getStringExtra(EXTRA_RESTAURANT_ID) ?: "demo_restaurant"
            val frag = WriteReviewFragment().apply {
                arguments = bundleOf("restaurantId" to rid)
            }
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainer, frag)
                .commit()
        }
    }
}
