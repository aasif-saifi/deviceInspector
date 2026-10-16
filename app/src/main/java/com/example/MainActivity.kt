package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.screens.HardwareViewModel
import com.example.screens.HardwareViewModelFactory
import com.example.screens.MainScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        
        setContent {
            // Instantiate the central hardware state viewmodel
            val hardwareViewModel: HardwareViewModel = viewModel(
                factory = HardwareViewModelFactory(application)
            )

            // Collect active styling configurations live
            val darkMode by hardwareViewModel.darkMode.collectAsStateWithLifecycle()
            val dynamicTheme by hardwareViewModel.dynamicTheme.collectAsStateWithLifecycle()

            MyApplicationTheme(
                darkMode = darkMode,
                dynamicColor = dynamicTheme
            ) {
                MainScreen(
                    viewModel = hardwareViewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
