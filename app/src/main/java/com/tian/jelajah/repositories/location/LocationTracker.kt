package com.tian.jelajah.repositories.location

import android.app.Activity
import android.location.Location
import androidx.lifecycle.LiveData
import com.tian.jelajah.repositories.LocalResponse

interface LocationTracker {
    fun getCurrentLocation(activity: Activity): LiveData<LocalResponse<Location?>>?
    fun stopUpdateLocation(activity: Activity)
}