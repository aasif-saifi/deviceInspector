package com.example.repository

import com.example.model.*
import com.example.utils.HardwareInfoProvider
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import org.json.JSONObject

class HardwareRepositoryImpl(
    private val infoProvider: HardwareInfoProvider
) : HardwareRepository {

    override fun getCpuInfo(): CpuInfo = infoProvider.getCpuInfo()

    override fun getDeviceInfo(): DeviceInfo = infoProvider.getDeviceInfo()

    override fun getSystemInfo(): SystemInfo = infoProvider.getSystemInfo()

    override fun getBatteryInfo(): BatteryInfo = infoProvider.getBatteryInfo()

    override fun getMemoryInfo(): MemoryInfo = infoProvider.getMemoryInfo()

    override fun getAvailableSensors(): List<SensorState> = infoProvider.getAvailableSensors()

    override fun observeSensor(sensorType: Int): Flow<List<Float>> = infoProvider.observeSensor(sensorType)

    override fun getDashboardData(
        cpuPct: Int,
        ramPct: Int,
        storagePct: Int,
        batteryPct: Int,
        batteryTemp: Float
    ): DashboardData {
        return infoProvider.getDashboardData(cpuPct, ramPct, storagePct, batteryPct, batteryTemp)
    }

    override suspend fun exportJsonReport(): String {
        val cpu = getCpuInfo()
        val device = getDeviceInfo()
        val system = getSystemInfo()
        val battery = getBatteryInfo()
        val memory = getMemoryInfo()

        val root = JSONObject().apply {
            put("reportGeneratedAt", System.currentTimeMillis())
            
            put("cpu", JSONObject().apply {
                put("model", cpu.model)
                put("manufacturer", cpu.manufacturer)
                put("architecture", cpu.architecture)
                put("abi", cpu.abi)
                put("coresCount", cpu.numberOfCores)
                put("governor", cpu.governor)
                put("overallUsagePercent", cpu.cpuUsagePercent)
                put("temperatureCelsius", cpu.temperatureCelsius ?: "N/A")
            })

            put("device", JSONObject().apply {
                put("manufacturer", device.manufacturer)
                put("brand", device.brand)
                put("model", device.model)
                put("board", device.board)
                put("bootloader", device.bootloader)
                put("screenResolution", device.screenResolution)
                put("refreshRateHz", device.refreshRateHz)
                put("densityDpi", device.densityDpi)
                put("vulkanSupport", device.vulkanSupport)
                put("nfcSupported", device.nfcSupported)
                put("wifiSupported", device.wifiSupported)
                put("bluetoothSupported", device.bluetoothSupported)
            })

            put("system", JSONObject().apply {
                put("androidVersion", system.androidVersion)
                put("sdkVersion", system.sdkVersion)
                put("buildNumber", system.buildNumber)
                put("securityPatch", system.securityPatch)
                put("kernelVersion", system.kernelVersion)
                put("isRooted", system.isRooted)
                put("selinuxStatus", system.selinuxStatus)
                put("uptimeMillis", system.uptimeMillis)
                put("installedApps", system.installedAppsCount)
            })

            put("battery", JSONObject().apply {
                put("percentage", battery.percentage)
                put("status", battery.status)
                put("health", battery.health)
                put("temperatureCelsius", battery.temperatureCelsius)
                put("voltageMilliVolts", battery.voltageMilliVolts)
                put("powerSource", battery.powerSource)
                put("chargeCycles", battery.chargeCycles ?: "N/A")
                put("chargingSpeedmA", battery.chargingSpeedMilliAmperes)
            })

            put("memory", JSONObject().apply {
                put("totalRamGb", String.format("%.2f", memory.totalRamBytes.toFloat() / (1024 * 1024 * 1024)))
                put("availableRamGb", String.format("%.2f", memory.availableRamBytes.toFloat() / (1024 * 1024 * 1024)))
                put("usedRamGb", String.format("%.2f", memory.usedRamBytes.toFloat() / (1024 * 1024 * 1024)))
                put("totalStorageGb", String.format("%.2f", memory.totalStorageBytes.toFloat() / (1024 * 1024 * 1024)))
                put("availableStorageGb", String.format("%.2f", memory.availableStorageBytes.toFloat() / (1024 * 1024 * 1024)))
                put("usedStorageGb", String.format("%.2f", memory.usedStorageBytes.toFloat() / (1024 * 1024 * 1024)))
            })
        }

        return root.toString(4) // 4 spaces indentation
    }
}
