package com.done.di

import com.done.data.repository.RoundsRepositoryImpl
import com.done.domain.repository.RoundsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton
    abstract fun bindRoundsRepository(impl: RoundsRepositoryImpl): RoundsRepository
}
