package com.tian.jelajah.di

import com.tian.jelajah.repositories.CommonRepository
import com.tian.jelajah.repositories.CommonRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCommonRepository(
        commonRepositoryImpl: CommonRepositoryImpl
    ): CommonRepository
}