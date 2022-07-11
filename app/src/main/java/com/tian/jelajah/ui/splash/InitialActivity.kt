package com.tian.jelajah.ui.splash

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.viewbinding.library.activity.viewBinding
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import com.google.android.gms.tasks.Task
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.tian.jelajah.data.pref.Preference
import com.tian.jelajah.databinding.ActivityInitialBinding
import com.tian.jelajah.receiver.ReminderReceiver
import com.tian.jelajah.ui.menu.MainMenuActivity
import com.tian.jelajah.services.GpsHelper
import com.tian.jelajah.utils.gotoActivityNewTask
import kotlinx.coroutines.*
import java.io.IOException
import java.util.concurrent.TimeUnit


class InitialActivity : AppCompatActivity(), MultiplePermissionsListener {
//    private val gps: LocationService by lazy { LocationService(this) }
    private val binding : ActivityInitialBinding by viewBinding()
    private val preference : Preference by lazy { Preference(this) }
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val TAG = this::class.java.simpleName
    private val REQUESTPERMISSIONLOCATION = 9000
    private val list: ArrayList<String> by lazy { ArrayList(preference.alarmCorrectionTime) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fusedLocationClient = getFusedLocationProviderClient(this)

        binding.run{
            btnFindMe.setOnClickListener {
                checkPermission()
            }
        }
    }

    private fun enableLoc() {
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener { locationSettingsResponse ->
            val states = locationSettingsResponse.locationSettingsStates
            if (states!!.isLocationPresent || checkLocationPermission()) {
                //Do something
                fusedLocationClient.requestLocationUpdates(
                    locationRequest, locationUpdates, Looper.getMainLooper())
            }
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException){
                try {
                    if (checkLocationPermission()) {
                        exception.startResolutionForResult(
                            this,
                            REQUESTPERMISSIONLOCATION
                        )
                    }
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }
    private val locationRequest = LocationRequest.create().apply {
        interval = TimeUnit.SECONDS.toMillis(2)
        fastestInterval = TimeUnit.SECONDS.toMillis(2)
        maxWaitTime = TimeUnit.SECONDS.toMillis(2)
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY

    }

    private val locationUpdates = object : LocationCallback() {
        override fun onLocationResult(lr: LocationResult) {
            preference.locationLatLongi = "${lr.lastLocation?.latitude}|${lr.lastLocation?.longitude}"
            if(lr.lastLocation != null){
                Log.e(TAG, "onLocationResult: ${preference.locationLatLongi}", )
                loadAddress()
            }
        }
    }

    private fun checkLocationPermission() : Boolean = (ContextCompat.checkSelfPermission(
        this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)

    @OptIn(DelicateCoroutinesApi::class)
    private fun loadAddress() {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val latAndLong = preference.locationLatLongi?.split("|")
                val lat = latAndLong?.get(0)?.toDouble()
                val longi = latAndLong?.get(1)?.toDouble()
                @Suppress("BlockingMethodInNonBlockingContext")
                val address =
                    GpsHelper.getLocationAddress(this@InitialActivity, lat!!, longi!!)
                address?.let {
                    Log.e("TAG", "loadAddress: ${it.subAdminArea} | $longi | $lat")
                    preference.city = it.subAdminArea
                    val alarmCorrection = arrayOf("0","0","0","0","0")
                    alarmCorrection.forEach { cor -> list.add(cor) }
                    preference.alarmCorrectionTime = list
                    preference.notifications = arrayListOf("imsak","fajr","sunrise","dhuha","dhuhr","asr","maghrib","isha")
                }
                GlobalScope.launch(Dispatchers.Main) {
                    if (address != null) {
                        fusedLocationClient.removeLocationUpdates(locationUpdates)
                        preference.isInitialize = true
                        ReminderReceiver.enableReminder(this@InitialActivity)
                        gotoActivityNewTask(MainMenuActivity::class)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun checkPermission() {
        Dexter.withContext(this).withPermissions(arrayListOf(
            ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION))
            .withListener(this)
            .onSameThread()
            .check()
    }

    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
        if (report!!.areAllPermissionsGranted()) {
            enableLoc()
        } else if (report.isAnyPermissionPermanentlyDenied) {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri: Uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivityForResult(intent, REQUESTPERMISSIONLOCATION)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e(TAG, "onActivityResult: $requestCode | $resultCode", )
        when (requestCode) {
            REQUESTPERMISSIONLOCATION-> when (resultCode) {
                RESULT_OK -> {
                    try {
                        fusedLocationClient.requestLocationUpdates(
                            locationRequest, locationUpdates, Looper.getMainLooper())
                    } catch (unlikely: SecurityException) {
                        Log.e(TAG, "Lost location permissions. Couldn't remove updates. $unlikely")
                    }
                }
                RESULT_CANCELED -> Log.d("abc","CANCEL")
            }
        }
    }

    override fun onPermissionRationaleShouldBeShown(p0: MutableList<PermissionRequest>?, token: PermissionToken?) {
        token!!.continuePermissionRequest()
    }

}