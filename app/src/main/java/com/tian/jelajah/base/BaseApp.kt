package com.tian.jelajah.base


import android.app.Application
import com.tian.jelajah.BuildConfig
import com.tian.jelajah.utils.Pref
import dagger.android.AndroidInjection.inject
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication


abstract class BaseApp : Application() {

    override fun onCreate() {
        super.onCreate()
        setupDefaultEnvironment()
    }

//    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
//        return DaggerAppComponent.builder().application(this).build().apply { inject(this@BaseApp) }
//    }

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