package com.tian.jelajah.di

import com.tian.jelajah.repositories.location.LocationTracker
import com.tian.jelajah.repositories.location.LocationTrackerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Module
@InstallIn(SingletonComponent::class)
abstract class LocationModule {

    @Binds
    @Singleton
    abstract fun bindLocationTracker(locationTrackerImpl: LocationTrackerImpl): LocationTracker

}