package com.hse.parkingapp.utils.parking

import android.content.SharedPreferences
import javax.inject.Inject

enum class Parking(val key: String) {
    BUILDING(key = "building_id"),
    LEVEL(key = "level_id")
}

class ParkingManager @Inject constructor(
    private val prefs: SharedPreferences
) {
    fun getBuildingId(): String? {
        return prefs.getString(Parking.BUILDING.key, null)
    }

    fun saveBuildingId(id: String?) {
        id?.let {
            prefs.edit()
                .putString(Parking.BUILDING.key, it)
                .apply()
        }
    }

    fun getLevelId(): String? {
        return prefs.getString(Parking.LEVEL.key, null)
    }

    fun saveLevelId(id: String?) {
        id?.let {
            prefs.edit()
                .putString(Parking.LEVEL.key, it)
                .apply()
        }
    }
}