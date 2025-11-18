package com.ratemyplate.work

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ratemyplate.data.repository.OfflineReviewRepository

class ReviewSyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    private val TAG = "ReviewSyncWorker"
    private val repo = OfflineReviewRepository(context)

    override suspend fun doWork(): Result {
        return try {
            Log.d(TAG, "Starting sync of pending reviews")
            repo.syncPendingReviews()
            Log.d(TAG, "Sync completed")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Sync failed", e)
            Result.retry()
        }
    }
}
