package com.tian.jelajah.services

import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.tian.jelajah.utils.Constants.LOCATION_WORKER
import java.util.concurrent.TimeUnit


object ServiceHelper {

    fun runWorker(context: Context) {
        val constrain = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val worker = PeriodicWorkRequestBuilder<LocationWork>(15, TimeUnit.MINUTES)
            .setConstraints(constrain)
            .addTag(LOCATION_WORKER)
            .build()

        WorkManager.getInstance(context).enqueue(listOf(worker))
    }
}