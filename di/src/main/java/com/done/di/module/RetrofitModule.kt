package com.done.di.module

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.done.di.qualifier.AppBaseUrl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    @Provides
    @AppBaseUrl
    fun provideRetrofit(
        @AppBaseUrl baseUrl: String,
        okHttpClient: OkHttpClient,
        factory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().serializeNulls().create()
                )
            )
            .addConverterFactory(factory)
            .build()
    }

    @Provides
    fun provideConverterFactory(gson: Gson): GsonConverterFactory {
        return GsonConverterFactory.create(gson)
    }

    @Provides
    fun provideGson(): Gson {
        val gsonBuilder = GsonBuilder()
        return gsonBuilder.create()
    }
}