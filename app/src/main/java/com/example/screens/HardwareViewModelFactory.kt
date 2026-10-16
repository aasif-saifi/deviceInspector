package com.example.screens

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.DeviceInspectorApplication

class HardwareViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HardwareViewModel::class.java)) {
            val app = application as DeviceInspectorApplication
            @Suppress("UNCHECKED_CAST")
            return HardwareViewModel(
                application = app,
                repository = app.repository,
                settingsDataStore = app.settingsDataStore
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
