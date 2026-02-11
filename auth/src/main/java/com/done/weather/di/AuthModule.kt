package com.done.weather.di

import com.done.weather.repository.AuthRepository
import com.done.weather.repository.AuthRepositoryImpl
import org.koin.dsl.module

val authModule = module {
    single<AuthRepository> {
        AuthRepositoryImpl(authApi = get())
    }
}
