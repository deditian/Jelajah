package com.tian.jelajah.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tian.jelajah.di.ViewModelFactory
import kotlin.reflect.KClass

fun <VM : ViewModel> AppCompatActivity.obtainViewModel(viewModel: KClass<VM>): Lazy<VM> {
    return lazy {
        val factory = ViewModelFactory.getInstance(this.application)
        return@lazy ViewModelProvider(this, factory).get(viewModel.java)
    }
}