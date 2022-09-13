package com.tian.jelajah.model

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class SurahListResponse (
    @SerializedName("data")
    val data: List<Surah>
) : GeneralResponse(), Parcelable

@Parcelize
@Entity
data class Surah(
    @SerializedName("number")
    val number: String,
    @SerializedName("ayahCount")
    val ayahCount: String,
    @SerializedName("sequence")
    val sequence: String,
    @SerializedName("asma")
    val asma: asma,
    @SerializedName("preBismillah")
    val preBismillah: pb? = null,
    @SerializedName("type")
    val type: ariden,
    @SerializedName("tafsir")
    val tafsir: enid,
    @SerializedName("recitation")
    val recitation: rc
) :  Parcelable

@Parcelize
data class pb(
    @SerializedName("text")
    val text: arRead,
    @SerializedName("translate")
    val translate: enid,
) : Parcelable


@Parcelize
data class arRead(
    @SerializedName("ar")
    val ar: String,
    @SerializedName("read")
    val read: String,
) : Parcelable


@Parcelize
data class ariden(
    @SerializedName("ar")
    val ar: String,
    @SerializedName("en")
    val en: String,
    @SerializedName("id")
    val id: String
) : Parcelable

@Parcelize
data class asma(
    @SerializedName("ar")
    val ar: arenid,
    @SerializedName("en")
    val en: arenid,
    @SerializedName("id")
    val id: arenid,
    @SerializedName("translation")
    val translation: enid
) : Parcelable

@Parcelize
data class arenid(
    @SerializedName("short")
    val short: String,
    @SerializedName("long")
    val long: String
) : Parcelable

@Parcelize
data class enid(
    @SerializedName("en")
    val en: String,
    @SerializedName("id")
    val id: String
): Parcelable

@Parcelize
data class rc(
    @SerializedName("full")
    val full: String,
): Parcelable