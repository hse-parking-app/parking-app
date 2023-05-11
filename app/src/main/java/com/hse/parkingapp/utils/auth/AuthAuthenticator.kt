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
        return runBlocking {
            val newAccessToken = getNewAccessToken()

            if (!newAccessToken.isSuccessful || newAccessToken.body() == null) {
                return@runBlocking null
            }

            newAccessToken.body()?.let {
                tokenManager.saveAccessToken(it.accessToken)

                response.request.newBuilder()
                    .header("Content-Type", "application/json")
                    .header("Accept-Encoding", "identity")
                    .header("Authorization", "Bearer ${tokenManager.getAccessToken()}")
                    .header("accept", "*/*")
                    .build()
            }
        }
    }

    private suspend fun getNewAccessToken(): retrofit2.Response<TokenResponse> {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient
            .Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(NetworkConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        val service = retrofit.create(AuthApi::class.java)
        return service.updateAccessToken(
            refreshRequest = RefreshRequest(tokenManager.getRefreshToken())
        )
    }
}