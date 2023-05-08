package com.hse.parkingapp.ui.buildings

import com.hse.parkingapp.model.Building

sealed class BuildingsEvent {
    class OnBuildingClick(val building: Building) : BuildingsEvent()
    object OnContinueClick : BuildingsEvent()
}
