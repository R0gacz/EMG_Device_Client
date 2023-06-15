package com.project.emg_device_client;
import java.util.HashMap;

public class GattAttributes {

    private static HashMap<String, String> attributes = new HashMap();

    public static String CLIENT_CHARACTERISTIC_CONFIG = "00001801-0000-1000-8000-00805f9b34fb";

    public static String UUID_GENERIC_ACCESS_SERVICE = "00001800-0000-1000-8000-00805f9b34fb";
    public static String UUID_DEVICE_INFORMATION = "0000180a-0000-1000-8000-00805f9b34fb";
    public static String UUID_TIMESTAMPED_DATA_SERVICE = "634f7246-d598-46d7-9e10-521163769295";
    public static String UUID_CURRENT_TIME_SERVICE = "00001805-0000-1000-8000-00805f9b34fb";
    public static String UUID_REFFERENCE_TIME_UPDATE_SERVICE = "00001806-0000-1000-8000-00805f9b34fb";
    public static String UUID_BATTERY_SERVICE = "0000180f-0000-1000-8000-00805f9b34fb";

    public static String UUID_DEVICE_NAME = "00002a00-0000-1000-8000-00805f9b34fb";
    public static String UUID_APPEARANCE = "00002a01-0000-1000-8000-00805f9b34fb";
    public static String UUID_CURRENT_TIME = "00002a2b-0000-1000-8000-00805f9b34fb";
    public static String UUID_EMG_MEASUREMENT = "634f7246-d598-46d7-9e10-521163769297";
    public static String UUID_PLX_MEASUREMENT = "634f7246-d598-46d7-9e10-521163769296";

    static {
        // Sample Services.
        attributes.put(UUID_GENERIC_ACCESS_SERVICE, "Generic Access");
        attributes.put(UUID_DEVICE_INFORMATION, "Device Device Information Service");
        attributes.put(UUID_TIMESTAMPED_DATA_SERVICE, "Timestamped Data Service");
        attributes.put(UUID_CURRENT_TIME_SERVICE, "Current Time Service");
        attributes.put(UUID_REFFERENCE_TIME_UPDATE_SERVICE, "Reference Time Update Service");
        attributes.put(UUID_BATTERY_SERVICE, "Battery Service");
        // Sample Characteristics.
        attributes.put(UUID_DEVICE_NAME, "Device Name");
        attributes.put(UUID_APPEARANCE, "Appearance");
        attributes.put(UUID_EMG_MEASUREMENT, "EMG Measurement");
        attributes.put(UUID_PLX_MEASUREMENT, "PLX Measurement");
        attributes.put(UUID_CURRENT_TIME, "Current Time");
        attributes.put(UUID_CURRENT_TIME, "Current Time");
    }
    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
