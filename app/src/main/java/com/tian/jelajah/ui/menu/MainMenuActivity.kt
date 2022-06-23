package com.tian.jelajah.ui.menu

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.AlertDialog
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*


class MainMenuActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainMenuBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val REQUEST_LOCATION_PERMISSION = 10100
    val PERMISSION_ID = 42
    //    private val viewModel: SurahViewModel by viewModels()
    private val TAG = this::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvMenus.apply {
            layoutManager = GridLayoutManager(this@MainMenuActivity, 2)
            adapter = this@MainMenuActivity.adapter
        }

    }


    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        setItemMenu()
    }

    override fun onResume() {
        super.onResume()
        findLocation()
    }


    fun findLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@MainMenuActivity)
        if (checkPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            fusedLocationClient?.lastLocation?.
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
                        Log.e(TAG, "onCreate: $lat")
                        val addresses = geocoder.getFromLocation(lat, longi, 1)
                        val cityName = addresses[0].locality
                        Log.e(TAG, "onLocationChanged: $cityName")
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
                val dialog = AlertDialog.Builder(this)
                    .setTitle("Permission")
                    .setMessage("Permission needed!")
                    .setPositiveButton("OK") { _, _ ->
                        ActivityCompat.requestPermissions(
                            this, perm, PERMISSION_ID
                        )
                    }
                    .setNegativeButton("No") { _, _ -> }
                    .create()
                dialog.show()
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

    private fun setItemMenu() {
        val array = ArrayList<Menus>()
        array.add(Menus(Color.BLUE, "Al-Quran"))
        array.add(Menus(Color.GREEN, "Doa-doa"))
        array.add(Menus(Color.RED, "Puasa Sunah"))
        array.add(Menus(Color.CYAN, "Berita"))
        array.add(Menus(Color.MAGENTA, "Pesantren di Indonesia"))
        array.add(Menus(Color.YELLOW, "Pahlawan Nasional Indonesia"))
        adapter.submitList(array)
    }

    private val adapter = MainMenuAdapter().apply {
        listener = object : MainMenuAdapter.RecyclerViewClickListener {
            override fun onItemClicked(view: View, item: Menus) {

            }
        }
    }

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
    }

    private fun stopPeriodic() {
        fusedLocationClient?.
        removeLocationUpdates(locationUpdates)
    }
}