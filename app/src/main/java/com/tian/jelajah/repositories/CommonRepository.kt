package com.tian.jelajah.repositories

import androidx.lifecycle.LiveData
import com.tian.jelajah.model.Prayer
import com.tian.jelajah.model.Surah

interface CommonRepository {
    fun getJadwalSholat(latAndLong : String) : LiveData<ApiResponse<List<Prayer>>>
    fun prayer(date: String) : LiveData<List<Prayer>>
    suspend fun loadApiPrayer(date: String) : List<Prayer>?
    fun getListSurah() : LiveData<ApiResponse<List<Surah>>>
}