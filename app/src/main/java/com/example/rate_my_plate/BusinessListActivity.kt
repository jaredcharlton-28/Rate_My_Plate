package com.example.rate_my_plate

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rate_my_plate.model.Business
import com.example.rate_my_plate.ui.theme.BusinessAdapter

class BusinessListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BusinessAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_list)

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewBusiness)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize Adapter with sample data
        adapter = BusinessAdapter(getSampleBusinesses()) { business ->
            // Handle click on a business item
            // e.g., navigate to details activity
        }

        recyclerView.adapter = adapter
    }

    // Sample data for testing
    private fun getSampleBusinesses(): MutableList<Business> {
        return mutableListOf(
            Business(
                "Pizza Place", "Restaurant", 4.5f, "https://via.placeholder.com/150",
                thumbnailUrl = TODO(),
                description = TODO()
            ),
            Business(
                "Burger Joint", "Fast Food", 4.0f, "https://via.placeholder.com/150",
                thumbnailUrl = TODO(),
                description = TODO()
            ),
            Business(
                "Coffee Corner", "Cafe", 4.8f, "https://via.placeholder.com/150",
                thumbnailUrl = TODO(),
                description = TODO()
            )
        )
    }
}
