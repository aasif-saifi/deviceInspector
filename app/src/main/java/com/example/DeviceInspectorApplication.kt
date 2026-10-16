package com.example

import android.app.Application
import com.example.data.SettingsDataStore
import com.example.repository.HardwareRepository
import com.example.repository.HardwareRepositoryImpl
import com.example.utils.HardwareInfoProvider

class DeviceInspectorApplication : Application() {
    
    lateinit var infoProvider: HardwareInfoProvider
    lateinit var repository: HardwareRepository
    lateinit var settingsDataStore: SettingsDataStore

    override fun onCreate() {
        super.onCreate()
        
        infoProvider = HardwareInfoProvider(this)
        repository = HardwareRepositoryImpl(infoProvider)
        settingsDataStore = SettingsDataStore(this)
    }
}
