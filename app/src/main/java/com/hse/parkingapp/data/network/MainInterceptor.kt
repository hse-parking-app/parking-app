package com.hse.parkingapp.data.network

import okhttp3.Interceptor
import okhttp3.Response

class MainInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Basic YWRtaW46YWRtaW4=")
            .addHeader("accept", "*/*")
            .build()
        return chain.proceed(request)
    }
}