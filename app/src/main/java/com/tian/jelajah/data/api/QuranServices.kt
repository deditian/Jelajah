package com.tian.jelajah.data.api

import retrofit2.Call
import retrofit2.http.GET

interface QuranServices {

    @GET("quran")
    fun getListSurah(): Call<String>

    @GET("quran/{surah}")
    fun getSurahQuran(surah : String): Call<String>

}