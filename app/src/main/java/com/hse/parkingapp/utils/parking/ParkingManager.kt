package com.hse.parkingapp.utils.parking

import android.content.SharedPreferences
import javax.inject.Inject

enum class Parking(val key: String) {
    BUILDING(key = "building_id"),
    LEVEL(key = "level_id"),
    HOURS(key = "hours"),
    CAR(key = "car")
}

class ParkingManager @Inject constructor(
    private val prefs: SharedPreferences,
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

    fun getHoursDifference(): Long {
        return prefs.getLong(Parking.HOURS.key, 0)
    }

    fun saveHoursDifference(difference: Long?) {
        difference?.let {
            prefs.edit()
                .putLong(Parking.HOURS.key, it)
                .apply()
        }
    }

    fun getCarId(): String? {
        return prefs.getString(Parking.CAR.key, null)
    }

    fun saveCarId(id: String?) {
        id?.let {
            prefs.edit()
                .putString(Parking.CAR.key, it)
                .apply()
        }
    }

    fun deleteCarId() {
        prefs.edit().remove(Parking.CAR.key).apply()
    }
}
