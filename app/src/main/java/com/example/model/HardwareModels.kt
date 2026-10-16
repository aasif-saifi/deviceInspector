package com.example.model

/**
 * Data structures representing all hardware and system metrics.
 */

data class CpuCoreState(
    val coreId: Int,
    val currentFrequencyHz: Long,
    val minFrequencyHz: Long,
    val maxFrequencyHz: Long,
    val usagePercent: Int
)

data class CpuInfo(
    val model: String,
    val manufacturer: String,
    val architecture: String,
    val abi: String,
    val numberOfCores: Int,
    val minFrequencyHz: Long,
    val maxFrequencyHz: Long,
    val governor: String,
    val supportedInstructionSets: List<String>,
    val cores: List<CpuCoreState>,
    val cpuUsagePercent: Int,
    val temperatureCelsius: Float?
)

data class DeviceInfo(
    val manufacturer: String,
    val brand: String,
    val model: String,
    val board: String,
    val bootloader: String,
    val hardware: String,
    val serial: String,
    val androidId: String,
    val screenResolution: String,
    val refreshRateHz: Float,
    val densityDpi: Int,
    val gpuRenderer: String,
    val gpuVendor: String,
    val openGlVersion: String,
    val vulkanSupport: Boolean,
    val cameraCount: Int,
    val frontCameraRes: String,
    val rearCameraRes: String,
    val hasFlash: Boolean,
    val fingerprintSupport: Boolean,
    val faceUnlockSupport: Boolean,
    val nfcSupported: Boolean,
    val usbSupported: Boolean,
    val bluetoothSupported: Boolean,
    val wifiSupported: Boolean
)

data class SystemInfo(
    val androidVersion: String,
    val sdkVersion: Int,
    val buildNumber: String,
    val securityPatch: String,
    val kernelVersion: String,
    val isRooted: Boolean,
    val selinuxStatus: String,
    val timezone: String,
    val language: String,
    val uptimeMillis: Long,
    val runningServicesCount: Int,
    val installedAppsCount: Int
)

data class BatteryInfo(
    val percentage: Int,
    val isCharging: Boolean,
    val status: String, // Charging, Discharging, Full, etc.
    val health: String, // Good, Overheat, Dead, etc.
    val temperatureCelsius: Float,
    val voltageMilliVolts: Int,
    val technology: String,
    val powerSource: String, // AC, USB, Wireless, Battery
    val chargeCycles: Int?,
    val chargingSpeedMilliAmperes: Int, // Charging current
    val estimatedRemainingTimeMinutes: Int // Estimated remaining time
)

data class MemoryInfo(
    val totalRamBytes: Long,
    val availableRamBytes: Long,
    val usedRamBytes: Long,
    val totalStorageBytes: Long,
    val availableStorageBytes: Long,
    val usedStorageBytes: Long,
    val externalTotalStorageBytes: Long?,
    val externalAvailableStorageBytes: Long?,
    val cacheSizeBytes: Long
)

data class SensorState(
    val id: Int,
    val name: String,
    val type: Int,
    val vendor: String,
    val version: Int,
    val maximumRange: Float,
    val resolution: Float,
    val power: Float,
    val currentValues: List<Float>,
    val isAvailable: Boolean
)

data class DashboardData(
    val cpuUsagePercent: Int,
    val ramUsagePercent: Int,
    val storageUsagePercent: Int,
    val batteryPercent: Int,
    val batteryTempCelsius: Float,
    val networkDownloadSpeedKbps: Double,
    val networkUploadSpeedKbps: Double,
    val uptimeString: String
)
