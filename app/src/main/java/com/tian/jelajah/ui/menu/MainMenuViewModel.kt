package com.tian.jelajah.ui.menu

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tian.jelajah.model.JadwalSholatRequest
import com.tian.jelajah.model.Prayer
import com.tian.jelajah.repositories.CommonRepository
import kotlinx.coroutines.launch
import java.util.concurrent.CountDownLatch

class MainMenuViewModel : ViewModel() {

    private val repository = CommonRepository()

    private var _jadwalSholat = MutableLiveData<String>()

    val responseJadwalSholat = Transformations.switchMap(_jadwalSholat) { repository.getJadwalSholat(it) }

    fun _jadwalSholat(latAndLong : String) = viewModelScope.launch {
        _jadwalSholat.value = latAndLong
    }
}