package com.tian.jelajah.ui.splash


import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.viewbinding.library.activity.viewBinding
import com.tian.jelajah.data.pref.Preference
import com.tian.jelajah.databinding.ActivitySplashBinding
import com.tian.jelajah.receiver.ReminderReceiver
import com.tian.jelajah.services.LocationService
import com.tian.jelajah.ui.menu.MainMenuActivity
import com.tian.jelajah.utils.gotoActivityNewTask
import kotlinx.coroutines.*
import java.io.IOException

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    val preference: Preference by lazy { Preference(this) }
    val gps: LocationService by lazy { LocationService(this) }
    private val binding: ActivitySplashBinding by viewBinding()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        Log.e("SplashActivity", "isReminder ${ReminderReceiver.isReminder(this)}")
        if (!ReminderReceiver.isReminder(this)) ReminderReceiver.updateAlarm(this)
        loadLatLong()
        countDownTimer.start()
    }

    private val countDownTimer = object : CountDownTimer(1000, 1000L) {
        override fun onTick(elapsedTime: Long) {}
        override fun onFinish() {
            Log.e("SplashActivity", "onFinish: loadLatLong() ${preference.isUpdateLocation}", )
            if (preference.isInitialize && preference.isUpdateLocation) {
                gotoActivityNewTask(MainMenuActivity::class)
            } else {
                gotoActivityNewTask(InitialActivity::class)
            }
        }
    }

    private fun loadLatLong() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                if (gps.location == null) {
                    if (gps.retry()) {
                        delay(1000)
                        loadLatLong()
                    }
                }
                val longi = gps.location!!.longitude
                val lat = gps.location!!.latitude

                @Suppress("BlockingMethodInNonBlockingContext")
                val address =
                    LocationService.getLocationAddress(this@SplashActivity, lat, longi)
                address?.let {
                    Log.e("SplashActivity", "loadLatLong: ${it.subAdminArea} | $longi | $lat")
                    val latAndLong = "$lat|$longi"
                    preference.locationLatLongi = latAndLong
                    preference.city = it.subAdminArea
                    gps.stopUsingGPS()
                }
                preference.isUpdateLocation = address != null

            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        countDownTimer.cancel()
    }
}