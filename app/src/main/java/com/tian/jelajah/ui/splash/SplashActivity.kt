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
import com.tian.jelajah.services.ServiceHelper
import com.tian.jelajah.ui.menu.MainMenuActivity
import com.tian.jelajah.utils.Constants.LOCATION_WORKER
import com.tian.jelajah.services.GpsHelper
import com.tian.jelajah.utils.gotoActivityNewTask
import com.tian.jelajah.utils.isWorkScheduled
import kotlinx.coroutines.*
import java.io.IOException

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    val preference: Preference by lazy { Preference(this) }
    private val binding: ActivitySplashBinding by viewBinding()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        Log.e("SplashActivity", "isReminder ${ReminderReceiver.isReminder(this)}")
        if (!ReminderReceiver.isReminder(this)) ReminderReceiver.updateAlarm(this)

        if(preference.isInitialize && !isWorkScheduled(this, LOCATION_WORKER))
            ServiceHelper.runWorker(this)
        else loadLatLong()

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
                val latAndLong = preference.locationLatLongi?.split("|")
                val lat = latAndLong?.get(0)?.toDouble()
                val longi = latAndLong?.get(1)?.toDouble()
                @Suppress("BlockingMethodInNonBlockingContext")
                val address =
                    GpsHelper.getLocationAddress(this@SplashActivity, lat!!, longi!!)
                address?.let {
                    Log.e("SplashActivity", "loadLatLong: ${it.subAdminArea} | $longi | $lat")
                    val latAndLong = preference.locationLatLongi

                    preference.city = it.subAdminArea
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