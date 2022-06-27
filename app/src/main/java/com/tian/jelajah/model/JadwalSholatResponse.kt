package com.tian.jelajah.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class JadwalSholatResponse(
    @SerializedName("data")
    val data: List<DataJadwal>
): GeneralResponse(), Parcelable

@Parcelize
data class DataJadwal(
    @SerializedName("tanggal")
    val tanggal: String,
    @SerializedName("jadwal")
    val jadwal: Jadwal
) : Parcelable

@Parcelize
data class Jadwal(
    @SerializedName("Subuh")
    val Subuh: String,
    @SerializedName("Terbit")
    val Terbit: String,
    @SerializedName("Duhur")
    val Duhur: String,
    @SerializedName("Ashar")
    val Ashar: String,
    @SerializedName("Terbenam")
    val Terbenam: String,
    @SerializedName("Maghrib")
    val Maghrib: String,
    @SerializedName("Isya")
    val Isya: String,
    @SerializedName("Imsak")
    val Imsak: String,
    @SerializedName("TengahMalam")
    val TengahMalam: String
) : Parcelable

data class JadwalSholatRequest(
    val lat: Double,
    val long : Double,
    val tahun : Int,
    val bulan : Int,
    val tanggal : Int
)
