package com.tian.jelajah.ui.menu

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.github.razir.progressbutton.DrawableButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.tian.jelajah.R
import com.tian.jelajah.databinding.ActivityMainMenuBinding
import com.tian.jelajah.model.Menus
import com.tian.jelajah.model.Prayer
import com.tian.jelajah.repositories.ApiResponse
import com.tian.jelajah.ui.quran.QuranActivity
import com.tian.jelajah.utils.*
import com.tian.jelajah.utils.Constants.BERITA
import com.tian.jelajah.utils.Constants.DOA
import com.tian.jelajah.utils.Constants.PAHLAWAN
import com.tian.jelajah.utils.Constants.PESANTREN
import com.tian.jelajah.utils.Constants.PUASA
import com.tian.jelajah.utils.Constants.QURAN
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*

class MainMenuActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainMenuBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val REQUEST_LOCATION_PERMISSION = 10100
    private val PERMISSION_ID = 42
    private val viewModel: MainMenuViewModel by viewModels()
    private val TAG = this::class.java.simpleName
    private var countDownTimer: CountDownTimer? = null
    private var prayers: List<Prayer>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        binding.rvMenus.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = this@MainMenuActivity.adapter
        }

        setItemMenu()

        binding.btnNotification.setOnClickListener {
            val item = prayers?.find { prayer -> prayer.time.isValid() }
            if (item != null) {
                if (item.alarm) {
//                    binding.btnNotification.setImageResource(if (item.type == "notify") R.drawable.ic_baseline_notifications_off_24 else R.drawable.ic_baseline_volume_off_24)

                } else {
//                    binding.btnNotification.setImageResource(if (item.type == "notify") R.drawable.ic_baseline_notifications_24 else R.drawable.ic_baseline_volume_up_24)
                }
//                ReminderReceiver.updateAlarm(this)
            }
        }

        viewModel.responseJadwalSholat.observe(this){
            when(it) {
                is ApiResponse.Error -> {
                    Log.e(TAG, "onCreate error: ${it.error}" )
                }
                ApiResponse.Loading -> {
                    showDialogProgress()
                }
                is ApiResponse.Success -> {
                    hideDialogProgress()
                    prayers = it.data.run {
                        val prayers = ArrayList<Prayer>()
                        forEach { prayer ->  prayers.add(prayer) }
                        return@run prayers
                    }
                    updatePrayer(it.data)
                }
            }
        }
    }

    fun showDialogProgress(){
        binding.pbMenu.spinKit.visibility = View.VISIBLE
    }

    fun hideDialogProgress (){
        binding.pbMenu.spinKit.visibility = View.GONE
    }



    private fun updatePrayer(prayer : List<Prayer>) {
        prayer.forEach {
            if (it.time.isValid()) {
                val string = "${getStringWithNameId(it.name)} ${dateFormat("HH:mm", it.time)}"
                CoroutineScope(Dispatchers.Main).launch {
                    binding.txtTimePrayer.text = string
                    binding.txtDate.text = dateFormatParse("EEEE, dd MMMM yyyy",it.date)
                }
                countDownTimer?.cancel()
                countDownTimer = null
                runCountDown(it.time)
                return
            }
        }
    }

    private fun runCountDown(time: Long?) {
        val countTimeLong: Long
        val dNow = Date()
        if (time == null) return else {
            if (time <= dNow.time) return
            countTimeLong = time - dNow.time
        }

        if (countDownTimer != null) return

        countDownTimer = object : CountDownTimer(countTimeLong, 1000L) {
            override fun onTick(elapsedTime: Long) {
                binding.txtCountdownPrayer.text = createTimeString(elapsedTime)
            }

            override fun onFinish() {
                nowTimePrayer {
                    updatePrayer(prayers!!)
                }
            }
        }
        countDownTimer?.start()
    }

    private fun nowTimePrayer(callbackFinish: () -> Unit) {
        binding.materialTextView2.setText(R.string.now_time_prayer)
        val colorTo = binding.txtCountdownPrayer.currentTextColor
        ValueAnimator.ofObject(ArgbEvaluator(), Color.TRANSPARENT, colorTo).apply {
            duration = 500L
            repeatCount = 10000 / duration.toInt()
            repeatMode = ValueAnimator.REVERSE
            addUpdateListener {
                if (it.animatedValue is Int) binding.txtCountdownPrayer.setTextColor(it.animatedValue as Int)
            }
            this.doOnEnd {
                callbackFinish.invoke()
                binding.materialTextView2.setText(R.string.next_time_prayer)
            }
            start()
        }
    }


    override fun onResume() {
        super.onResume()
        findLocation()
    }

    private fun setItemMenu() {
        val array = ArrayList<Menus>()
        array.add(Menus(Color.WHITE, QURAN))
        array.add(Menus(Color.WHITE, DOA))
        array.add(Menus(Color.WHITE, PUASA))
        array.add(Menus(Color.WHITE, BERITA))
        array.add(Menus(Color.WHITE, PESANTREN))
        array.add(Menus(Color.WHITE, PAHLAWAN))
        adapter.submitList(array)
    }

    private val adapter = MainMenuAdapter().apply {
        listener = object : MainMenuAdapter.RecyclerViewClickListener {
            override fun onItemClicked(view: View, item: Menus) {

                when (item.name){
                    QURAN ->{
                        gotoActivity(QuranActivity::class)
                    }
                    DOA ->{

                    }
                    PUASA ->{

                    }
                    BERITA ->{

                    }
                    PESANTREN ->{

                    }
                    PAHLAWAN ->{

                    }
                }
            }
        }
    }


    private fun findLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@MainMenuActivity)
        if (checkPermission(
                ACCESS_COARSE_LOCATION,
                ACCESS_FINE_LOCATION)) {
            fusedLocationClient.lastLocation.
            addOnSuccessListener(this
            ) { location: Location? ->
                // Got last known location. In some rare
                // situations this can be null.
                binding.txtCurrentLocation.showProgress {
                    this.progressColor = Color.WHITE
                    gravity = DrawableButton.GRAVITY_CENTER
                }
                if (location == null) {
                    binding.txtCurrentLocation.let { btn ->
                        btn.isEnabled = true
                        btn.hideProgress(getString(R.string.find_location))
                    }
                    startLocationRequests()
                } else location.apply {
                    // Handle location object
                    Log.e("LOG", location.toString())
                    val geocoder = Geocoder(this@MainMenuActivity, Locale.getDefault())
                    try {
                        val lat = location.latitude
                        val longi = location.longitude
                        val latAndLong = "$lat|$longi"
                        viewModel._jadwalSholat(latAndLong)
                        val addresses = geocoder.getFromLocation(lat, longi, 1)
                        val cityName = addresses[0].adminArea
                        CoroutineScope(Dispatchers.Main).launch {
                            binding.txtCurrentLocation.let { btn ->
                                btn.isEnabled = true
                                btn.hideProgress(getString(R.string.find_location))
                            }
                            binding.txtCurrentLocation.text = cityName
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun checkPermission(vararg perm:String) : Boolean {
        val havePermissions = perm.toList().all {
            ContextCompat.checkSelfPermission(this,it) ==
                    PackageManager.PERMISSION_GRANTED
        }
        if (!havePermissions) {
            if(perm.toList().any {
                    ActivityCompat.
                    shouldShowRequestPermissionRationale(this, it)}
            ) {
                alert("Permission", "Permission needed!") {
                    positiveButton("Ok") {
                        ActivityCompat.requestPermissions(
                            this@MainMenuActivity, perm, PERMISSION_ID
                        )
                    }
                    negativeButton("Cancel"){}
                }.show()
            } else {
                ActivityCompat.requestPermissions(this, perm, PERMISSION_ID)
            }
            return false
        }
        return true
    }

    private fun startLocationRequests() {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener { locationSettingsResponse ->
            val states = locationSettingsResponse.locationSettingsStates
            if (states!!.isLocationPresent) {
                //Do something
                findLocation()
            }
           
            if (checkLocationPermission()) {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest, locationUpdates, Looper.getMainLooper())
            }
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException){
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    // IF STATEMENT THAT PREVENTS THE DIALOG FROM PROMPTING.
                    if (checkLocationPermission()) {
                        exception.startResolutionForResult(
                            this,
                            REQUEST_LOCATION_PERMISSION
                        )
                    }
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> when (resultCode) {
                RESULT_OK -> {
                    findLocation()
                    Toast.makeText(this, "Location enabled by user", Toast.LENGTH_LONG)
                        .show()
                }
                RESULT_CANCELED -> {
                    finish()
                    Toast.makeText(this, "Location not enabled, user cancelled.", Toast.LENGTH_LONG).show()
                }
                else -> {
                }
            }
        }
    }

    private fun checkLocationPermission() : Boolean = (ContextCompat.checkSelfPermission(
        this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)


    private val locationUpdates = object : LocationCallback() {
        override fun onLocationResult(lr: LocationResult) {
            Log.e("LOG", lr.toString())
            Log.e("LOG", "Newest Location: " + lr.locations.last())
            // do something with the new location...
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopPeriodic()
        countDownTimer?.cancel()
    }

    private fun stopPeriodic() {
        fusedLocationClient.removeLocationUpdates(locationUpdates)
    }
}