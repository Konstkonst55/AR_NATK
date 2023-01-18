package com.example.ar_natk.di

import com.example.ar_natk.data.repository.AppRepository
import com.example.ar_natk.data.repository.IAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataProvider {
    @Singleton
    @Provides
    fun provideIAuth(appRepository: AppRepository): IAuth {
        return appRepository
    }
}