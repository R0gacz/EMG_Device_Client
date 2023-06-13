package com.project.emg_device_client;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;

public class AvailableDevice {

    public BluetoothDevice device;
    public String name;
    public int rssi;
    public String macAddress;

    public AvailableDevice(ScanResult scanResult){
        device = scanResult.getDevice();
        name = device.getName();
        macAddress = device.getAddress();
        rssi = scanResult.getRssi();
    }
}
