package com.tian.jelajah.di

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory private constructor(application: Application) :
    ViewModelProvider.NewInstanceFactory() {

    private val mApplication: Application = application

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isInstance(Application::class.java)) {
            val newInstance = modelClass.getConstructor(Application::class.java)
            newInstance.newInstance(mApplication)
        } else {
            modelClass.newInstance()
        }
    }

    companion object {
        private lateinit var viewModelFactory: ViewModelFactory

        fun getInstance(application: Application): ViewModelFactory {
            if (!this::viewModelFactory.isInitialized) {
                viewModelFactory = ViewModelFactory(application)
            }
            return viewModelFactory
        }
    }

}