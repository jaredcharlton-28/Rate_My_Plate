package com.ratemyplate.work

import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

fun enqueueReviewSync(context: Context) {
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    val syncRequest = OneTimeWorkRequestBuilder<ReviewSyncWorker>()
        .setConstraints(constraints)
        .build()

    WorkManager.getInstance(context).enqueue(syncRequest)
}
