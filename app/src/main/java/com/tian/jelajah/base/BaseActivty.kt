package com.tian.jelajah.base

import android.os.Bundle
import dagger.android.support.DaggerAppCompatActivity

abstract class BaseActivty : DaggerAppCompatActivity() {


    abstract fun initData(savedInstanceState: Bundle?)

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        initData(savedInstanceState)
    }
}