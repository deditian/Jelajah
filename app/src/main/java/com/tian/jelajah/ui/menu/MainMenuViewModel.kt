package com.tian.jelajah.ui.menu

import android.app.Application
import androidx.lifecycle.*
import com.tian.jelajah.model.JadwalSholatRequest
import com.tian.jelajah.model.Prayer
import com.tian.jelajah.repositories.CommonRepository
import kotlinx.coroutines.launch
import java.util.concurrent.CountDownLatch

class MainMenuViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CommonRepository(application)

    private var _jadwalSholat = MutableLiveData<String>()

    val responseJadwalSholat = Transformations.switchMap(_jadwalSholat) { repository.getJadwalSholat(it) }

    fun _jadwalSholat(latAndLong : String) = viewModelScope.launch {
        _jadwalSholat.value = latAndLong
    }
}