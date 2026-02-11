package com.done.weather.di

import com.done.weather.ui.compose_ui.camera_screen_vision.CameraViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        CameraViewModel(
            detector = get(),
            tracker = get(),
            logRepository = get(),
            camifeyeRepository = get()
        )
    }
}