package com.tian.jelajah.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Base64

object Pref {

    const val ENV = "env"
    private var sharedPreferences: SharedPreferences? = null
    fun init(context: Context?) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun getString(key: String?): String {
        return String(Base64.decode(sharedPreferences!!.getString(key, ""), 0))
    }

    fun setString(key: String?, value: String) {
        val editor = sharedPreferences!!.edit()
        editor.putString(key, Base64.encodeToString(value.toByteArray(), 0))
        editor.apply()
    }

    fun getInt(key: String?): Int {
        return sharedPreferences!!.getInt(key, 0)
    }

    fun setInt(key: String?, value: Int) {
        val editor = sharedPreferences!!.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun getBool(key: String?): Boolean {
        return sharedPreferences!!.getBoolean(key, false)
    }

    fun setBool(key: String?, value: Boolean) {
        val editor = sharedPreferences!!.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getLong(key: String?): Long {
        return sharedPreferences!!.getLong(key, 0)
    }

    fun setLong(key: String?, value: Long) {
        val editor = sharedPreferences!!.edit()
        editor.putLong(key, value)
        editor.apply()
    }
}