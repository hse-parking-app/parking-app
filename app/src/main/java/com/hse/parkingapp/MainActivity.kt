package com.hse.parkingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import com.hse.parkingapp.navigation.NavGraph
import com.hse.parkingapp.ui.theme.ParkingAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ParkingAppTheme {
                Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                    NavGraph()
                }
            }
        }
    }
}
