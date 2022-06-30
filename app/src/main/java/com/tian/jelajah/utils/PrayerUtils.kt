package com.tian.jelajah.utils

import com.tian.jelajah.data.pref.Preference
import com.tian.jelajah.model.Prayer

class PrayerUtils(private val preference: Preference) {

    fun correctionTimingPrayer(prayer: Prayer) : Prayer {
        val correction = preference.alarmCorrectionTime
        return if (prayer.name != "sunrise" && prayer.name != "dhuha" && prayer.name != "imsak" ) {
            val time = correction[when(prayer.name) {
                "fajr" -> 0
                "dhuhr" -> 1
                "asr" -> 2
                "maghrib" -> 3
                "isha" -> 4
                else -> 0
            }].toInt()
            prayer.copy(time = prayer.time + (time * 60000))
        } else prayer
    }
}