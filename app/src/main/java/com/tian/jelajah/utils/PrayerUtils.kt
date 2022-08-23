package com.tian.jelajah.utils

import com.tian.jelajah.data.pref.Preferences
import com.tian.jelajah.model.Prayer

class PrayerUtils(private val preference: Preferences) {

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

    fun correctionTimingPrayers(list: List<Prayer>) : List<Prayer> {
        val prayers = ArrayList<Prayer>(list)
        val correction = preference.alarmCorrectionTime
        list.filter { it.name != "sunrise" && it.name != "dhuha" && it.name != "imsak" }.forEachIndexed { i, v ->
            val time = correction[i].toInt()
            val index = prayers.indexOf(v)
            prayers.remove(v)
            prayers.add(index, v.copy(time = v.time + (time * 60000)))
        }
        return prayers
    }
}