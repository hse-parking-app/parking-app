package com.hse.parkingapp.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.hse.parkingapp.data.network.AuthApi
import com.hse.parkingapp.data.network.ParkingApi
import com.hse.parkingapp.data.repository.AuthRepository
import com.hse.parkingapp.data.repository.ParkingRepository
import com.hse.parkingapp.utils.auth.AuthAuthenticator
import com.hse.parkingapp.utils.auth.AuthInterceptor
import com.hse.parkingapp.utils.auth.NetworkConstants
import com.hse.parkingapp.utils.parking.ParkingManager
import com.hse.parkingapp.utils.token.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(app: Application): SharedPreferences {
        return app.getSharedPreferences("prefs", MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideTokenManager(preferences: SharedPreferences): TokenManager =
        TokenManager(preferences)

    @Provides
    @Singleton
    fun provideParkingManager(preferences: SharedPreferences): ParkingManager =
        ParkingManager(preferences)

    @Provides
    @Singleton
    fun provideAuthInterceptor(tokenManager: TokenManager): AuthInterceptor =
        AuthInterceptor(tokenManager)

    @Provides
    @Singleton
    fun provideAuthAuthenticator(tokenManager: TokenManager): AuthAuthenticator =
        AuthAuthenticator(tokenManager)

    @Provides
    @Singleton
    fun provideOkHttpClient(
        interceptor: AuthInterceptor,
        authenticator: AuthAuthenticator,
    ): OkHttpClient = OkHttpClient.Builder().apply {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        addInterceptor(interceptor)
        addInterceptor(logging)
        authenticator(authenticator)
    }.build()

    @Provides
    @Singleton
    fun provideRetrofitBuilder(): Retrofit.Builder = Retrofit.Builder()
        .baseUrl(NetworkConstants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())

    @Provides
    @Singleton
    fun provideParkingApi(
        okHttpClient: OkHttpClient,
        retrofit: Retrofit.Builder,
    ): ParkingApi =
        retrofit
            .client(okHttpClient)
            .build()
            .create(ParkingApi::class.java)

    @Provides
    @Singleton
    fun provideAuthApi(
        okHttpClient: OkHttpClient,
        retrofit: Retrofit.Builder,
    ): AuthApi =
        retrofit
            .client(okHttpClient)
            .build()
            .create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideAuthRepository(
        api: AuthApi,
        tokenManager: TokenManager,
        parkingManager: ParkingManager,
    ): AuthRepository {
        return AuthRepository(api, tokenManager, parkingManager)
    }

    @Provides
    @Singleton
    fun provideParkingRepository(api: ParkingApi): ParkingRepository {
        return ParkingRepository(api)
    }
}
