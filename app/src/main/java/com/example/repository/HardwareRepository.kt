package com.example.repository

import com.example.model.*
import kotlinx.coroutines.flow.Flow

interface HardwareRepository {
    fun getCpuInfo(): CpuInfo
    fun getDeviceInfo(): DeviceInfo
    fun getSystemInfo(): SystemInfo
    fun getBatteryInfo(): BatteryInfo
    fun getMemoryInfo(): MemoryInfo
    fun getAvailableSensors(): List<SensorState>
    fun observeSensor(sensorType: Int): Flow<List<Float>>
    fun getDashboardData(cpuPct: Int, ramPct: Int, storagePct: Int, batteryPct: Int, batteryTemp: Float): DashboardData
    suspend fun exportJsonReport(): String
}
