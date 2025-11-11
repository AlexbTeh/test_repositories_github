package com.done.di

import com.done.data.api.ScorecardApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = "https://devapi.verifeye.info/api-vgps/"

    @Provides @Singleton
    fun provideLogging(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    @Provides @Singleton
    fun provideHeaders(): Interceptor = Interceptor { chain ->
        val req = chain.request().newBuilder()
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            // .addHeader("Authorization", "Bearer ...") // если понадобится
            .build()
        chain.proceed(req)
    }

    @Provides @Singleton
    fun provideOkHttp(
        logging: HttpLoggingInterceptor,
        headers: Interceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(headers)
        .addInterceptor(logging)
        .build()

    @Provides @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides @Singleton
    fun provideScorecardApi(retrofit: Retrofit): ScorecardApi =
        retrofit.create(ScorecardApi::class.java)
}
