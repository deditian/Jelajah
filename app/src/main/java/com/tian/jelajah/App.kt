package com.tian.jelajah

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.tian.jelajah.base.BaseApp
import com.tian.jelajah.utils.Constants
import com.tian.jelajah.utils.Pref
import java.util.*

class App  : BaseApp(){
    override fun onCreate() {
        super.onCreate()
        setupDefaultEnvironment()
    }

    override fun setupDefaultEnvironment() {
        super.setupDefaultEnvironment()
        registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

                val lang= Pref.getString(Constants.LANGUAGE)
                val language = if (lang.isNotEmpty()) lang.lowercase()
                else {
                    Pref.setString(Constants.LANGUAGE, "in")
                    "in"
                }

                val locale = Locale(language)
                Locale.setDefault(locale)

                val res = activity.resources
                val dm = res.displayMetrics
                val conf = res.configuration
                conf.locale = locale
                res.updateConfiguration(conf, dm)
            }

            override fun onActivityStarted(activity: Activity) {}

            override fun onActivityResumed(activity: Activity) {}

            override fun onActivityPaused(activity: Activity) {}

            override fun onActivityStopped(activity: Activity) {}

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

            override fun onActivityDestroyed(activity: Activity) {}

        })

    }
}