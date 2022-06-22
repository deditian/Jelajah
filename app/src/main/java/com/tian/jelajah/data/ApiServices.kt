package com.tian.jelajah.data

import retrofit2.Call
import retrofit2.http.GET

interface ApiServices {

    @GET("quran")
    fun getListSurah(): Call<String>

    @GET("quran/{surah}")
    fun getSurahQuran(surah : String): Call<String>

}