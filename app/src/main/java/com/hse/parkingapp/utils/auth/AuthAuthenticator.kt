package com.hse.parkingapp.utils.auth

import com.hse.parkingapp.data.network.AuthApi
import com.hse.parkingapp.utils.token.TokenManager
import com.hse.parkingapp.utils.token.TokenResponse
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class AuthAuthenticator @Inject constructor(
    private val tokenManager: TokenManager
): Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val refreshToken = tokenManager.getRefreshToken()

        return runBlocking {
            val newAccessToken = getNewAccessToken(refreshToken)

            if (!newAccessToken.isSuccessful || newAccessToken.body() == null) {
                tokenManager.deleteAccessToken()
                tokenManager.deleteRefreshToken()
            }

            newAccessToken.body()?.let {
                tokenManager.saveAccessToken(it.accessToken)
                tokenManager.saveRefreshToken(it.refreshToken)
                response.request.newBuilder()
                    .header("Accept-Encoding", "identity")
                    .header("Authorization", "Bearer ${it.accessToken}")
                    .build()
            }
        }
    }

    private suspend fun getNewAccessToken(refreshToken: String?): retrofit2.Response<TokenResponse> {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient
            .Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://91.185.85.37:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
        val service = retrofit.create(AuthApi::class.java)
        return service.updateAccessToken(
            refreshRequest = RefreshRequest(refreshToken)
        )
    }
}