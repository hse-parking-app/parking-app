package com.hse.parkingapp.utils.token

import android.content.SharedPreferences
import javax.inject.Inject

/**
 * Enum class representing tokens.
 * @param key The key associated with the token.
 */
enum class Token(val key: String) {
    /**
     * The access token.
     */
    ACCESS(key = "access"),

    /**
     * The refresh token.
     */
    REFRESH(key = "refresh")
}

/**
 * Manager class for handling access and refresh tokens.
 * @param prefs The shared preferences instance for storing tokens.
 */
class TokenManager @Inject constructor(
    private val prefs: SharedPreferences
) {
    /**
     * Retrieves the access token from shared preferences.
     * @return The access token if available, null otherwise.
     */
    fun getAccessToken(): String? {
        return prefs.getString(Token.ACCESS.key, null)
    }

    /**
     * Retrieves the refresh token from shared preferences.
     * @return The refresh token if available, null otherwise.
     */
    fun getRefreshToken(): String? {
        return prefs.getString(Token.REFRESH.key, null)
    }

    /**
     * Saves the access token to shared preferences.
     * @param token The access token to save.
     */
    fun saveAccessToken(token: String?) {
        token?.let {
            prefs.edit()
                .putString(Token.ACCESS.key, it)
                .apply()
        }
    }

    /**
     * Saves the refresh token to shared preferences.
     * @param token The refresh token to save.
     */
    fun saveRefreshToken(token: String?) {
        token?.let {
            prefs.edit()
                .putString(Token.REFRESH.key, it)
                .apply()
        }
    }

    /**
     * Deletes the access token from shared preferences.
     */
    fun deleteAccessToken() {
        prefs.edit().remove(Token.ACCESS.key).apply()
    }

    /**
     * Deletes the refresh token from shared preferences.
     */
    fun deleteRefreshToken() {
        prefs.edit().remove(Token.REFRESH.key).apply()
    }
}