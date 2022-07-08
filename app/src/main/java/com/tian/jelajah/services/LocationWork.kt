package com.tian.jelajah.services

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.tian.jelajah.utils.logi
import java.util.concurrent.Executor

class LocationWork(private val context: Context, parameters: WorkerParameters) : Worker(context, parameters) {

    @SuppressLint("RestrictedApi")
    override fun getBackgroundExecutor(): Executor {
        return Executor {
            logi("location work is running")
            GpsHelper.getLocation(context)
        }
    }

    override fun doWork(): Result {
        return Result.retry()
    }

}