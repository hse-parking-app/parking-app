package com.hse.parkingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hse.parkingapp.navigation.NavGraph
import com.hse.parkingapp.ui.theme.ParkingAppTheme
import com.hse.parkingapp.utils.auth.AuthResult
import com.hse.parkingapp.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel: MainViewModel = hiltViewModel()

//            installSplashScreen().apply {
//                setKeepVisibleCondition {
//
//                }
//            }

            ParkingAppTheme {
                Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                    NavGraph(viewModel = hiltViewModel())
                }
            }
        }
    }
}
