package com.example.rate_my_plate.ui.business

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rate_my_plate.R
import com.example.rate_my_plate.data.api.ApiClient
import com.example.rate_my_plate.data.model.Business
import com.example.rate_my_plate.ui.review.ReviewsActivity
import kotlinx.coroutines.launch

class BusinessListFragment : Fragment(R.layout.fragment_business_list) {

    private lateinit var rv: RecyclerView
    private lateinit var empty: TextView
    private lateinit var adapter: BusinessAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv = view.findViewById(R.id.rvBusinesses)
        empty = view.findViewById(R.id.emptyView)

        adapter = BusinessAdapter(mutableListOf()) { business ->
            startActivity(
                Intent(requireContext(), ReviewsActivity::class.java).apply {
                    putExtra(ReviewsActivity.EXTRA_RESTAURANT_ID, business.id)
                    putExtra(ReviewsActivity.EXTRA_RESTAURANT_NAME, business.name)
                    putExtra("ownerId", business.ownerId)
                }
            )
        }
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        fetchBusinesses()
    }

    private fun fetchBusinesses() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val resp = ApiClient.service.getBusinesses()
                val list: List<Business> =
                    if (resp.isSuccessful) resp.body().orEmpty() else emptyList()
                adapter.submit(list)                              // <-- changed
                empty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            } catch (_: Exception) {
                adapter.submit(emptyList())                       // <-- changed
                empty.visibility = View.VISIBLE
            }
        }
    }
}
