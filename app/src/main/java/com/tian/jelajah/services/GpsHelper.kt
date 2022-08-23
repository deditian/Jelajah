package com.tian.jelajah.services

import android.annotation.SuppressLint
import android.content.Context
import android.location.*
import android.os.Bundle
import android.util.Log
import com.tian.jelajah.data.pref.Preferences
import com.tian.jelajah.utils.logi
import java.io.IOException
import java.util.*

object GpsHelper {
    private const val TAG = "GpsHelper"

    @Throws(IOException::class)
    fun getLocationAddress(context: Context, lat: Double, lang: Double): Address? {
        val decoder = Geocoder(context, Locale.getDefault())
        val data: List<Address> = decoder.getFromLocation(lat, lang, 1)
        return when (data.isNotEmpty()) {
            true -> data[0]
            false -> null
        }
    }

    @SuppressLint("ServiceCast", "MissingPermission")
    fun getLocation(context: Context) {
        var preference = Preferences(context)
        var locationGps: Location? = null
        var locationNetwork: Location? = null
        val locationManager = context.applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (hasGps || hasNetwork) {
            if (hasGps) {
                Log.e(TAG, "getLocation: hasGps")
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0F, object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        logi("hasGps -> $location")
                        locationGps = location
                        preference.locationLatLongi = "${location.latitude}|${location.longitude}"
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

                    }

                    override fun onProviderEnabled(provider: String) {

                    }

                    override fun onProviderDisabled(provider: String) {

                    }

                })

                val localGpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (localGpsLocation != null) {
                    logi("localGpsLocation -> $localGpsLocation")
                    preference.locationLatLongi = "${localGpsLocation.latitude}|${localGpsLocation.longitude}"
                    locationGps = localGpsLocation
                }
            }
            if (hasNetwork) {
                Log.e("TAG", "getLocation: hasNetwork")
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 15000, 0F, object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        logi("hasNetwork -> $hasNetwork")
                        locationNetwork = location
                        preference.locationLatLongi = "${location.latitude}|${location.longitude}"
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

                    }

                    override fun onProviderEnabled(provider: String) {

                    }

                    override fun onProviderDisabled(provider: String) {

                    }

                })

                val localNetworkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (localNetworkLocation != null) {
                    Log.e(TAG, "getLocation: localNetworkLocation -> $localNetworkLocation")
                    locationNetwork = localNetworkLocation
                    preference.locationLatLongi = "${localNetworkLocation.latitude}|${localNetworkLocation.longitude}"
                }
            }

            if(locationGps != null && locationNetwork!= null){
                val gpsAccuracy = locationGps?.accuracy
                val networkAccuracy = locationNetwork?.accuracy
                if(gpsAccuracy!! > networkAccuracy!!){
                    logi("locationNetwork perbandingan -> $locationNetwork")
                    val networkLatitude = locationNetwork?.latitude
                    val networkLongitude = locationNetwork?.longitude
                    preference.locationLatLongi = "$networkLatitude|$networkLongitude"
                }else{
                    val gpsLatitude = locationGps?.latitude
                    val gpsLongitude = locationGps?.longitude
                    if (gpsLatitude == 0.0 && gpsLongitude == 0.0){
                        logi("locationNetwork else perbandingan -> $locationNetwork")
                        val networkLatitude = locationNetwork?.latitude
                        val networkLongitude = locationNetwork?.longitude
                        preference.locationLatLongi = "$networkLatitude|$networkLongitude"
                    } else {
                        logi("locationGps else perbandingan -> $locationGps")
                        preference.locationLatLongi = "$gpsLatitude|$gpsLongitude"
                    }
                }
            }

        }

    }
}