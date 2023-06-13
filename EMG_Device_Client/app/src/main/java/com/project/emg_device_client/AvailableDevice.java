package com.project.emg_device_client;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;

public class AvailableDevice {

    public BluetoothDevice device;
    public String name;
    public int rssi;
    public String macAddress;

    @SuppressLint("MissingPermission")
    public AvailableDevice(ScanResult scanResult){
        device = scanResult.getDevice();
        name = device.getName();
        macAddress = device.getAddress();
        rssi = scanResult.getRssi();
    }
}
