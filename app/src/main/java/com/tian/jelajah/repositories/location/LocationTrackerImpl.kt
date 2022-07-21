package com.tian.jelajah.repositories.location

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.tian.jelajah.data.pref.Preference
import com.tian.jelajah.repositories.LocalResponse
import com.tian.jelajah.utils.Constants.REQUESTPERMISSIONLOCATION
import com.tian.jelajah.utils.loge
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@ExperimentalCoroutinesApi
class LocationTrackerImpl @Inject constructor(
    private val locationClient: FusedLocationProviderClient,
    private val application: Application,
    private val preference: Preference
): LocationTracker {

    private val TAG = this::class.java.simpleName

    private val resultData = MutableLiveData<LocalResponse<Location?>>()

    private val locationUpdates = object : LocationCallback() {
        override fun onLocationResult(lr: LocationResult) {
            preference.locationLatLongi = "${lr.lastLocation?.latitude}|${lr.lastLocation?.longitude}"
            if (lr.lastLocation != null) {
                Log.e(TAG, "onLocationResult: ${preference.locationLatLongi}",)
                resultData.value = LocalResponse.Success(lr.lastLocation)
            }
        }
    }

    override fun getCurrentLocation(activity: Activity): LiveData<LocalResponse<Location?>>? {
        Log.e(TAG, "getCurrentLocation: " )
        val hasAccessFineLocationPermission = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasAccessCoarseLocationPermission = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val locationManager = application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if(!hasAccessCoarseLocationPermission || !hasAccessFineLocationPermission || !isGpsEnabled) {
            Log.e(TAG, "getCurrentLocation: hasAccessCoarseLocationPermission")
            val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
            builder.setAlwaysShow(true)
            val client: SettingsClient = LocationServices.getSettingsClient(application)
            val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
            task.addOnSuccessListener { locationSettingsResponse ->
                val states = locationSettingsResponse.locationSettingsStates
                if (states!!.isLocationPresent) {
                    //Do something
                    locationClient.requestLocationUpdates(
                        locationRequest, locationUpdates, Looper.getMainLooper())
                }
            }
            task.addOnFailureListener {exception ->
                if (exception is ResolvableApiException){
                    try {
                            resultData.value = LocalResponse.Error(exception.localizedMessage)
                            exception.startResolutionForResult(
                                activity,
                                REQUESTPERMISSIONLOCATION)
                    } catch (sendEx: IntentSender.SendIntentException) {
                        // Ignore the error.
                    }
                }
            }
        }

        locationClient.lastLocation.apply {
                if(isSuccessful) {
                    result.let {
                        Log.e(TAG, "getCurrentLocation:it:: $it", )
                        resultData.value = LocalResponse.Success(it)
                    }
                } else {
                    try {
                        locationClient.requestLocationUpdates(
                            locationRequest, locationUpdates, Looper.getMainLooper())
                    } catch (unlikely: SecurityException) {
                        Log.e(TAG, "Lost location permissions. Couldn't remove updates. $unlikely")
                    }
                }
            addOnSuccessListener {
                resultData.value = LocalResponse.Success(it)
            }
            addOnFailureListener {
                resultData.value = LocalResponse.Error(it.localizedMessage)
            }
        }

        return resultData
    }

    private val locationRequest = LocationRequest.create().apply {
        interval = TimeUnit.SECONDS.toMillis(10)
        fastestInterval = TimeUnit.SECONDS.toMillis(15)
        maxWaitTime = TimeUnit.SECONDS.toMillis(2)
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    override fun stopUpdateLocation(activity: Activity) {
        locationClient.removeLocationUpdates(locationUpdates)
    }
}