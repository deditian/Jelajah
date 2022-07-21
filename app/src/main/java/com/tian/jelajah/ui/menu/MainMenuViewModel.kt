package com.tian.jelajah.ui.menu

import android.app.Activity
import androidx.lifecycle.*
import com.tian.jelajah.repositories.location.LocationTracker
import com.tian.jelajah.model.Prayer
import com.tian.jelajah.repositories.CommonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainMenuViewModel @Inject constructor(
    val repository : CommonRepository,
    val locationTracker: LocationTracker) : ViewModel() {

    private var _jadwalSholat = MutableLiveData<String>()
    val responseJadwalSholat = Transformations.switchMap(_jadwalSholat) { repository.getJadwalSholat(it) }

    fun _jadwalSholat(latAndLong : String) = viewModelScope.launch {
        _jadwalSholat.value = latAndLong
    }


    private val prayers = MutableLiveData<String>()
    val responsePrayers: LiveData<List<Prayer>> = Transformations.switchMap(prayers) { repository.prayer(it) }

    fun prayers(nextDate: String = "")  = viewModelScope.launch {
        prayers.postValue(nextDate)
    }

    private val location = MutableLiveData<Activity>()
    val responseLocation = Transformations.switchMap(location) { locationTracker.getCurrentLocation(it) }

    fun location(activity: Activity)  = viewModelScope.launch {
        location.value = activity
    }

    fun stopLocation(activity: Activity) = locationTracker.stopUpdateLocation(activity)




}