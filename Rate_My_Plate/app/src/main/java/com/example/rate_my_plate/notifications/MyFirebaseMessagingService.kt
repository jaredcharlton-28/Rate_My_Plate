package com.ratemyplate.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ratemyplate.R
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.content.Intent

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val TAG = "FCMService"
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(TAG, "Message received: ${message.data}")
        // Show notification in system tray
        showNotification("RateMyPlate", message.notification?.body ?: "New update")

        // Send local broadcast to UI to refresh reviews
        val intent = Intent("com.ratemyplate.NEW_REVIEW")
        intent.putExtra("restaurantId", message.data["restaurantId"])
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun showNotification(title: String, body: String) {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "ratemyplate_updates"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(channelId, "Updates", NotificationManager.IMPORTANCE_DEFAULT)
            nm.createNotificationChannel(ch)
        }
        val n = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_notification) // add icon
            .setAutoCancel(true)
            .build()
        nm.notify(System.currentTimeMillis().toInt(), n)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM token: $token")
        // Optionally send token to your backend
    }
}
