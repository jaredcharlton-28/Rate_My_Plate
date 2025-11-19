package com.example.rate_my_plate.ui.business

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rate_my_plate.R
import com.example.rate_my_plate.data.repository.BusinessRepository
import com.example.rate_my_plate.ui.review.ReviewsActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class BusinessListFragment : Fragment(R.layout.fragment_business_list) {

    private lateinit var rv: RecyclerView
    private lateinit var empty: TextView
    private lateinit var progress: ProgressBar
    private lateinit var adapter: BusinessAdapter
    private val repository = BusinessRepository()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv = view.findViewById(R.id.rvBusinesses)
        empty = view.findViewById(R.id.emptyView)
        progress = view.findViewById(R.id.progress)

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
            progress.visibility = View.VISIBLE
            empty.visibility = View.GONE
            try {
                val (list, fromCache) = repository.getBusinessesWithCache()
                adapter.submit(list)
                empty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                if (fromCache && list.isNotEmpty()) {
                    Snackbar.make(requireView(), R.string.offline_cached_businesses, Snackbar.LENGTH_LONG)
                        .show()
                }
            } catch (_: Exception) {
                adapter.submit(emptyList())
                empty.visibility = View.VISIBLE
            } finally {
                progress.visibility = View.GONE
            }
        }
    }
}
