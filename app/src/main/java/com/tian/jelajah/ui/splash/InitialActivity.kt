package com.tian.jelajah.ui.splash

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.viewbinding.library.activity.viewBinding
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import com.tian.jelajah.base.BaseActivty
import com.tian.jelajah.data.pref.Preferences
import com.tian.jelajah.databinding.ActivityInitialBinding
import com.tian.jelajah.receiver.ReminderReceiver
import com.tian.jelajah.repositories.ApiResponse
import com.tian.jelajah.repositories.LocalResponse
import com.tian.jelajah.ui.menu.MainMenuActivity
import com.tian.jelajah.services.GpsHelper
import com.tian.jelajah.ui.menu.MainMenuViewModel
import com.tian.jelajah.utils.gotoActivityNewTask
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.io.IOException

@AndroidEntryPoint
class InitialActivity : BaseActivty() {
    private val binding : ActivityInitialBinding by viewBinding()
    private val preference : Preferences by lazy { Preferences(this) }
    private val TAG = this::class.java.simpleName
    private val list: ArrayList<String> by lazy { ArrayList(preference.alarmCorrectionTime) }
    private val viewModel: MainMenuViewModel by viewModels()

    override fun initData(savedInstanceState: Bundle?) {
        setContentView(binding.root)
        binding.btnFindMe.setOnClickListener {
            if (hasPermissions(this, PERMISSIONS)) {
                viewModel.location(this)
            } else {
                permReqLauncher.launch(PERMISSIONS)
            }
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        viewModel.responseLocation.observe(this){
            when(it) {
                is LocalResponse.Error -> {
                    hideProgressDialog()
                    Log.e(TAG, "onCreate error: ${it.error}" )

                }
                LocalResponse.Loading -> {
                    showProgressDialog()
                }
                is LocalResponse.Success -> {
                    hideProgressDialog()
                    val data = it.data?.accuracy
                    if (data != null){
                        Log.e(TAG, "onCreate Success: ${it.data}" )
                        loadAddress()
                    }
                }
            }
        }


        viewModel.responseJadwalSholat.observe(this){
            when(it) {
                is ApiResponse.Error -> {
                    hideProgressDialog()
                    Log.e(TAG, "onCreate error: ${it.error}" )
                }
                ApiResponse.Loading -> {
                    showProgressDialog()
                }
                is ApiResponse.Success -> {
                    hideProgressDialog()
                    if (it.data.isNotEmpty()){
                        val alarmCorrection = arrayOf("0","0","0","0","0")
                        alarmCorrection.forEach { cor -> list.add(cor) }
                        preference.alarmCorrectionTime = list
                        preference.notifications = arrayListOf("imsak","fajr","sunrise","dhuha","dhuhr","asr","maghrib","isha")
                        preference.isInitialize = true
                        ReminderReceiver.enableReminder(this@InitialActivity)
                        gotoActivityNewTask(MainMenuActivity::class)
                    }
                }
            }
        }


    }


    private val PERMISSIONS = arrayOf(
        ACCESS_FINE_LOCATION,
        ACCESS_COARSE_LOCATION
    )

    private val permReqLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all {
                it.value == true
            }
            if (granted) {
                viewModel.location(this)
            }
        }

    private fun hasPermissions(context: Context, permissions: Array<String>): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }


    private fun loadAddress() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val latAndLong = preference.locationLatLongi?.split("|")
                val lat = latAndLong?.get(0)?.toDouble()
                val longi = latAndLong?.get(1)?.toDouble()
                @Suppress("BlockingMethodInNonBlockingContext")
                val address =
                    GpsHelper.getLocationAddress(this@InitialActivity, lat!!, longi!!)
                address?.let {
                    Log.e(TAG, "loadAddress: ${it.subAdminArea} | $longi | $lat")
                    preference.city = it.subAdminArea
                }
                if (address != null) {
                    viewModel.stopLocation(this@InitialActivity)
                    viewModel._jadwalSholat(preference.locationLatLongi!!)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


}