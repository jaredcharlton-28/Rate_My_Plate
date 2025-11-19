package com.example.rate_my_plate.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.rate_my_plate.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class ReviewMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Token could be sent to backend in future; for now we just log implicitly.
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val title = message.notification?.title ?: getString(R.string.notification_new_review_title)
        val body = message.notification?.body
            ?: message.data["body"]
            ?: getString(R.string.notification_new_review_body)

        createChannel()
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification)

        message.data[DATA_BUSINESS_ID]?.let { businessId ->
            broadcastNewReview(businessId)
        }
    }

    private fun broadcastNewReview(businessId: String) {
        val intent = Intent(ACTION_REVIEW_UPDATE).apply {
            putExtra(EXTRA_BUSINESS_ID, businessId)
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            NotificationManagerCompat.from(this).createNotificationChannel(channel)
        }
    }

    companion object {
        const val ACTION_REVIEW_UPDATE = "com.example.rate_my_plate.REVIEW_UPDATE"
        const val EXTRA_BUSINESS_ID = "businessId"
        const val DATA_BUSINESS_ID = "businessId"
        private const val CHANNEL_ID = "reviews"
        private const val NOTIFICATION_ID = 1001
    }
}
