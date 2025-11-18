package com.example.rate_my_plate

import android.app.Application
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.rate_my_plate.network.ApiClient


class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        registerNetworkCallback()
    }

    private fun registerNetworkCallback() {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val request = NetworkRequest.Builder().build()

        cm.registerNetworkCallback(request, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                // When network available, try to upload pending reviews
                // We need access to ReviewRepository here. If using DI (Hilt) use that.
                // Simple approach: launch a LocalScope that creates repository (needs ApiService)
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        // Obtain ApiService instance (singleton). Replace with your Api creation.
                        val api = ApiClient.create() // implement ApiClient
                        val repo = com.example.rate_my_plate.data.repository.ReviewRepository(api, applicationContext)
                        repo.uploadPendingReviews()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        })
    }
}
