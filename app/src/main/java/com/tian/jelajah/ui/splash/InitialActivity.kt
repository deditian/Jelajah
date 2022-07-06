package com.tian.jelajah.ui.splash

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.viewbinding.library.activity.viewBinding
import androidx.activity.viewModels
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.tian.jelajah.R
import com.tian.jelajah.data.pref.Preference
import com.tian.jelajah.databinding.ActivityInitialBinding
import com.tian.jelajah.receiver.ReminderReceiver
import com.tian.jelajah.services.LocationService
import com.tian.jelajah.ui.menu.MainMenuActivity
import com.tian.jelajah.ui.menu.MainMenuViewModel
import com.tian.jelajah.utils.gotoActivityNewTask
import kotlinx.coroutines.*
import java.io.IOException

class InitialActivity : AppCompatActivity(), MultiplePermissionsListener {
    private val gps: LocationService by lazy { LocationService(this) }
    private val binding : ActivityInitialBinding by viewBinding()
    private val viewModel : MainMenuViewModel by viewModels()
    private val preference : Preference by lazy { Preference(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run{
            btnFindMe.setOnClickListener { checkPermission() }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun loadAddress() {
        GlobalScope.launch(Dispatchers.Main) {
            delay(100)
            if (gps.location == null) {
                if (gps.retry()) {
                    delay(1000)
                    loadAddress()
                }
            }
            try {
                val longi = gps.location!!.longitude
                val lat = gps.location!!.latitude
                @Suppress("BlockingMethodInNonBlockingContext")
                val address =
                    LocationService.getLocationAddress(this@InitialActivity, lat, longi)
                address?.let {
                    Log.e("TAG", "loadAddress: ${it.subAdminArea} | $longi | $lat")
                    val latAndLong = "$lat|$longi"
                    preference.city = it.subAdminArea
                    preference.locationLatLongi = latAndLong
                    preference.notifications = arrayListOf("imsak","fajr","sunrise","dhuha","dhuhr","asr","maghrib","isha")
                    ReminderReceiver.enableReminder(this@InitialActivity)
                    gps.stopUsingGPS()
                }

                GlobalScope.launch(Dispatchers.Main) {
                    if (address != null) {
                        preference.isInitialize = true
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 9000) checkPermission()
    }

    private fun checkPermission() {
        Dexter.withContext(this).withPermissions(arrayListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION))
            .withListener(this)
            .onSameThread()
            .check()
    }

    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
        if (report!!.areAllPermissionsGranted()) {
            gps.callServices()
            loadAddress()
        } else if (report.isAnyPermissionPermanentlyDenied) {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri: Uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivityForResult(intent, 9000)
        }
    }

    override fun onPermissionRationaleShouldBeShown(p0: MutableList<PermissionRequest>?, token: PermissionToken?) {
        token!!.continuePermissionRequest()
    }

    override fun onDestroy() {
        super.onDestroy()
        gps.stopUsingGPS()
    }
}