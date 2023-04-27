package com.hse.parkingapp.utils.token

import android.content.SharedPreferences
import javax.inject.Inject

enum class Token(val key: String) {
    ACCESS(key = "access"),
    REFRESH(key = "refresh")
}

class TokenManager @Inject constructor(
    private val prefs: SharedPreferences
) {
    fun getAccessToken(): String? {
        return prefs.getString(Token.ACCESS.key, null)
    }

    fun getRefreshToken(): String? {
        return prefs.getString(Token.REFRESH.key, null)
    }

    fun saveAccessToken(token: String?) {
        token?.let {
            prefs.edit()
                .putString(Token.ACCESS.key, it)
                .apply()
        }
    }

    fun saveRefreshToken(token: String?) {
        token?.let {
            prefs.edit()
                .putString(Token.REFRESH.key, it)
                .apply()
        }
    }

    fun deleteAccessToken() {
        prefs.edit().remove(Token.ACCESS.key).apply()
    }

    fun deleteRefreshToken() {
        prefs.edit().remove(Token.REFRESH.key).apply()
    }
}