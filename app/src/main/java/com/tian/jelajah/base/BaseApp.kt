package com.tian.jelajah.base

import android.app.Application
import com.tian.jelajah.BuildConfig
import com.tian.jelajah.utils.Pref


abstract class BaseApp : Application() {

    override fun onCreate() {
        super.onCreate()
        setupDefaultEnvironment()
    }

    open fun setupDefaultEnvironment() {
        Pref.init(this.applicationContext)

        // hardcode the env here:
        if(BuildConfig.DEBUG) {
//            if (Pref.getInt(Pref.ENV) == 0)
//                Pref.setInt(
//                Pref.ENV, Config.DEV_VM1)
        } else {
//            Pref.setInt(Pref.ENV, Config.PROD)
        }
    }

}