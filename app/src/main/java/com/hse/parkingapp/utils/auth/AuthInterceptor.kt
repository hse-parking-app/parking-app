package com.hse.parkingapp.utils.auth

import com.hse.parkingapp.utils.token.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenManager.getAccessToken()
        val request = chain.request().newBuilder()
        request
            .addHeader("Accept-Encoding", "identity")
            .addHeader("Authorization", "Bearer $token")
        return chain.proceed(request.build())
    }
}
