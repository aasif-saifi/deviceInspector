package com.example.screens

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.SettingsDataStore
import com.example.model.*
import com.example.repository.HardwareRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HardwareViewModel(
    application: Application,
    private val repository: HardwareRepository,
    private val settingsDataStore: SettingsDataStore
) : AndroidViewModel(application) {

    // --- State Holders ---
    private val _cpuState = MutableStateFlow<CpuInfo?>(null)
    val cpuState: StateFlow<CpuInfo?> = _cpuState.asStateFlow()

    private val _deviceState = MutableStateFlow<DeviceInfo?>(null)
    val deviceState: StateFlow<DeviceInfo?> = _deviceState.asStateFlow()

    private val _systemState = MutableStateFlow<SystemInfo?>(null)
    val systemState: StateFlow<SystemInfo?> = _systemState.asStateFlow()

    private val _batteryState = MutableStateFlow<BatteryInfo?>(null)
    val batteryState: StateFlow<BatteryInfo?> = _batteryState.asStateFlow()

    private val _memoryState = MutableStateFlow<MemoryInfo?>(null)
    val memoryState: StateFlow<MemoryInfo?> = _memoryState.asStateFlow()

    private val _sensorsList = MutableStateFlow<List<SensorState>>(emptyList())
    val sensorsList: StateFlow<List<SensorState>> = _sensorsList.asStateFlow()

    private val _dashboardState = MutableStateFlow<DashboardData?>(null)
    val dashboardState: StateFlow<DashboardData?> = _dashboardState.asStateFlow()

    // --- History Lists (for Realtime Canvas Graphs) ---
    private val _cpuHistory = MutableStateFlow<List<Int>>(emptyList())
    val cpuHistory: StateFlow<List<Int>> = _cpuHistory.asStateFlow()

    private val _ramHistory = MutableStateFlow<List<Int>>(emptyList())
    val ramHistory: StateFlow<List<Int>> = _ramHistory.asStateFlow()

    private val _batteryTempHistory = MutableStateFlow<List<Float>>(emptyList())
    val batteryTempHistory: StateFlow<List<Float>> = _batteryTempHistory.asStateFlow()

    // --- Live Sensor Observations ---
    private val _activeSensorType = MutableStateFlow<Int?>(null)
    val activeSensorType: StateFlow<Int?> = _activeSensorType.asStateFlow()

    private val _liveSensorValues = MutableStateFlow<List<Float>>(emptyList())
    val liveSensorValues: StateFlow<List<Float>> = _liveSensorValues.asStateFlow()

    // --- Setting States ---
    val darkMode = settingsDataStore.darkModeFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "SYSTEM")
    val dynamicTheme = settingsDataStore.dynamicThemeFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    val tempUnit = settingsDataStore.tempUnitFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "CELSIUS")
    val freqUnit = settingsDataStore.freqUnitFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "GHZ")
    val language = settingsDataStore.languageFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "en")

    private var monitoringJob: Job? = null
    private var sensorJob: Job? = null

    init {
        // Fetch static specs initially
        refreshStaticInfo()
        
        // Start live hardware monitoring loop
        startMonitoring()
    }

    private fun refreshStaticInfo() {
        viewModelScope.launch {
            _deviceState.value = repository.getDeviceInfo()
            _systemState.value = repository.getSystemInfo()
            _sensorsList.value = repository.getAvailableSensors()
        }
    }

    private fun startMonitoring() {
        monitoringJob?.cancel()
        monitoringJob = viewModelScope.launch {
            while (true) {
                try {
                    // Query sensors and system specs at slower rates, live stats at high speed
                    val cpu = repository.getCpuInfo()
                    val battery = repository.getBatteryInfo()
                    val memory = repository.getMemoryInfo()

                    _cpuState.value = cpu
                    _batteryState.value = battery
                    _memoryState.value = memory

                    // Dashboard dynamic update
                    _dashboardState.value = repository.getDashboardData(
                        cpuPct = cpu.cpuUsagePercent,
                        ramPct = if (memory.totalRamBytes > 0) {
                            ((memory.usedRamBytes.toFloat() / memory.totalRamBytes.toFloat()) * 100).toInt().coerceIn(0, 100)
                        } else 25,
                        storagePct = if (memory.totalStorageBytes > 0) {
                            ((memory.usedStorageBytes.toFloat() / memory.totalStorageBytes.toFloat()) * 100).toInt().coerceIn(0, 100)
                        } else 45,
                        batteryPct = battery.percentage,
                        batteryTemp = battery.temperatureCelsius
                    )

                    // Track histories (Max 30 items)
                    updateHistoryLists(cpu.cpuUsagePercent, memory, battery.temperatureCelsius)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                delay(1000) // Poll stats every second
            }
        }
    }

    private fun updateHistoryLists(cpuUsage: Int, memory: MemoryInfo, batteryTemp: Float) {
        // CPU History
        val currentCpuList = _cpuHistory.value.toMutableList()
        currentCpuList.add(cpuUsage)
        if (currentCpuList.size > 30) currentCpuList.removeAt(0)
        _cpuHistory.value = currentCpuList

        // RAM History
        val ramUsagePct = if (memory.totalRamBytes > 0) {
            ((memory.usedRamBytes.toFloat() / memory.totalRamBytes.toFloat()) * 100).toInt().coerceIn(0, 100)
        } else 0
        val currentRamList = _ramHistory.value.toMutableList()
        currentRamList.add(ramUsagePct)
        if (currentRamList.size > 30) currentRamList.removeAt(0)
        _ramHistory.value = currentRamList

        // Battery Temp History
        val currentBatteryList = _batteryTempHistory.value.toMutableList()
        currentBatteryList.add(batteryTemp)
        if (currentBatteryList.size > 30) currentBatteryList.removeAt(0)
        _batteryTempHistory.value = currentBatteryList
    }

    fun observeSensorRealtime(sensorType: Int?) {
        sensorJob?.cancel()
        _activeSensorType.value = sensorType
        _liveSensorValues.value = emptyList()

        if (sensorType != null) {
            sensorJob = viewModelScope.launch {
                repository.observeSensor(sensorType)
                    .collect { values ->
                        _liveSensorValues.value = values
                    }
            }
        }
    }

    // --- Settings Actions ---
    fun updateDarkMode(mode: String) {
        viewModelScope.launch {
            settingsDataStore.setDarkMode(mode)
        }
    }

    fun updateDynamicTheme(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setDynamicTheme(enabled)
        }
    }

    fun updateTempUnit(unit: String) {
        viewModelScope.launch {
            settingsDataStore.setTempUnit(unit)
        }
    }

    fun updateFreqUnit(unit: String) {
        viewModelScope.launch {
            settingsDataStore.setFreqUnit(unit)
        }
    }

    fun updateLanguage(lang: String) {
        viewModelScope.launch {
            settingsDataStore.setLanguage(lang)
        }
    }

    // --- Manual Pull-to-Refresh ---
    fun forceRefreshAll() {
        refreshStaticInfo()
        startMonitoring()
    }

    // --- Report Exporters ---
    suspend fun getJsonReport(): String {
        return repository.exportJsonReport()
    }

    override fun onCleared() {
        super.onCleared()
        monitoringJob?.cancel()
        sensorJob?.cancel()
    }
}
