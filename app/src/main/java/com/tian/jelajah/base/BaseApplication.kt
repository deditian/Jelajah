package com.tian.jelajah.base

import android.os.Bundle
import dagger.android.support.DaggerAppCompatActivity

abstract class BaseApplication : DaggerAppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

}