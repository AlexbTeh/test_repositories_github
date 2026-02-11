package com.done.weather.di

import com.done.weather.domain.usecase.RestartAndClearAppUseCase
import com.done.weather.domain.usecase.RestartAppUseCase
import com.done.weather.domain.usecase.SendRoundUseCase
import com.done.weather.domain.usecase.TrackGroupUseCase
import com.done.weather.domain.usecase.CamifeyeBootstrapUseCase
import org.koin.dsl.module

val useCaseModule = module {
    single<RestartAndClearAppUseCase> {
        RestartAndClearAppUseCase(
            appLifecycleManager = get(),
            imageLoader = get()
        )
    }
    single<RestartAppUseCase> {
        RestartAppUseCase(
            appLifecycleManager = get(),
            imageLoader = get()
        )
    }
    single {
        TrackGroupUseCase(
            noPeopleThresholdMs = 60_000L,
            cooldownMs = 60_000L
        )
    }

    single {
        CamifeyeBootstrapUseCase(
            repo = get()
        )
    }

    single {
        SendRoundUseCase(
            repo = get()
        )
    }
}