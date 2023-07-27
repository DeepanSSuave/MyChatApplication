package com.example.mychatapplication.di

import com.example.mychatapplication.BuildConfig
import com.example.mychatapplication.Network
import com.example.mychatapplication.di.RetrofitModule.baseURL
import com.example.mychatapplication.model.repositary.NetworkRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    const val baseURL : String = "http://192.168.5.42:8005/"

    @Singleton
    @Provides
    fun retrofitProvider(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseURL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun okHttpClientProvider(preferenceManager: ChatPreferenceManager): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.MINUTES)
            .writeTimeout(2, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addInterceptor { chain ->
                chain.proceed(chain.request().newBuilder().also {
                    preferenceManager.getString("AccessToken").let { token ->
                        it.addHeader(
                            "Authorization",
                            "Bearer $token"
                        )
                    }
                }.build())
            }
            .retryOnConnectionFailure(true)
            .build()

    @Singleton
    @Provides
    fun apiProvider(retrofit: Retrofit): Network {
        return retrofit.create(Network::class.java)
    }

    @Singleton
    @Provides
    fun repoProvider(api: Network, preferenceManager: ChatPreferenceManager) =
        NetworkRepository(api, preferenceManager)
}