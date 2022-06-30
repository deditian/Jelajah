package com.tian.jelajah.data.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServices {

    @GET("quran")
    fun getListSurah(): Call<String>

    @GET("quran/{surah}")
    fun getSurahQuran(surah : String): Call<String>

    //?lat=-6.1953184&long=106.792654&tahun=2022&bulan=6&tanggal=23
    @GET("/")
    fun getJadwalSholat(
        @Query("lat") lat: Double,
        @Query("long") long: Double,
        @Query("tahun") tahun: Int,
        @Query("bulan") bulan: Int,
        @Query("tanggal") tanggal: Int
    ): Call<String>
}