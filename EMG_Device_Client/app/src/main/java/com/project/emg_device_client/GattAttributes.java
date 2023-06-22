package com.project.emg_device_client;
import java.util.HashMap;

public class GattAttributes {

    private static HashMap<String, String> attributes = new HashMap();

    public static final String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    public static final String UUID_GATT_PROFILE_SERVICE = "00001801-0000-1000-8000-00805f9b34fb";
    public static final String UUID_GENERIC_ACCESS_SERVICE = "00001800-0000-1000-8000-00805f9b34fb";
    public static final String UUID_DEVICE_INFORMATION_SERVICE = "0000180a-0000-1000-8000-00805f9b34fb";
    public static final String UUID_TIMESTAMPED_DATA_SERVICE = "634f7246-d598-46d7-9e10-521163769295";
    public static final String UUID_CURRENT_TIME_SERVICE = "00001805-0000-1000-8000-00805f9b34fb";
    public static final String UUID_REFERENCE_TIME_UPDATE_SERVICE = "00001806-0000-1000-8000-00805f9b34fb";
    public static final String UUID_BATTERY_SERVICE = "0000180f-0000-1000-8000-00805f9b34fb";
    public static final String UUID_SILABS_OTA_SERVICE = "1d14d6ee-fd63-4fa1-bfa4-8f47b42119f0";

    public static final String UUID_DEVICE_NAME = "00002a00-0000-1000-8000-00805f9b34fb";
    public static final String UUID_APPEARANCE = "00002a01-0000-1000-8000-00805f9b34fb";
    public static final String UUID_MANUFACTURER_NAME_STRING = "00002a29-0000-1000-8000-00805f9b34fb";
    public static final String UUID_SYSTEM_ID = "00002a23-0000-1000-8000-00805f9b34fb";
    public static final String UUID_EMG_MEASUREMENT = "634f7246-d598-46d7-9e10-521163769297";
    public static final String UUID_PULSE_MEASUREMENT = "b6d1d0dc-86b6-4d1e-96f6-7e9dfb632c96";
    public static final String UUID_SA02_MEASUREMENT = "d0e8e624-e5fd-4356-a531-5df15f37830d";
    public static final String UUID_TIMESTAMP = "2fe487f1-2011-4280-bc1f-deae24311032";
    public static final String UUID_CURRENT_TIME = "00002a2b-0000-1000-8000-00805f9b34fb";
    public static final String UUID_LOCAL_TIME_INFO = "00002a0f-0000-1000-8000-00805f9b34fb";
    public static final String UUID_TIME_UPDATE_CONTROL_POINT = "00002a16-0000-1000-8000-00805f9b34fb";
    public static final String UUID_TIME_UPDATE_STATE = "00002a17-0000-1000-8000-00805f9b34fb";
    public static final String UUID_REFERENCE_TIME_INFO = "00002a14-0000-1000-8000-00805f9b34fb";
    public static final String UUID_BATTERY_LEVEL = "00002a19-0000-1000-8000-00805f9b34fb";
    public static final String UUID_SILABS_OTA_CONTROL = "f7bf3564-fb6d-4e53-88a4-5e37e0326063";

    static {
        // Sample Services.
        attributes.put(UUID_GATT_PROFILE_SERVICE, "GATT Profile Service");
        attributes.put(UUID_GENERIC_ACCESS_SERVICE, "Generic Access");
        attributes.put(UUID_DEVICE_INFORMATION_SERVICE, "Device Device Information Service");
        attributes.put(UUID_TIMESTAMPED_DATA_SERVICE, "Timestamped Data Service");
        attributes.put(UUID_CURRENT_TIME_SERVICE, "Current Time Service");
        attributes.put(UUID_REFERENCE_TIME_UPDATE_SERVICE, "Reference Time Update Service");
        attributes.put(UUID_BATTERY_SERVICE, "Battery Service");
        attributes.put(UUID_SILABS_OTA_SERVICE, "Silicon Labs OTA");
        // Sample Characteristics.
        attributes.put(UUID_DEVICE_NAME, "Device Name");
        attributes.put(UUID_APPEARANCE, "Appearance");
        attributes.put(UUID_MANUFACTURER_NAME_STRING, "Manufacturer Name String");
        attributes.put(UUID_SYSTEM_ID, "System ID");
        attributes.put(UUID_EMG_MEASUREMENT, "EMG Measurement");
        attributes.put(UUID_PULSE_MEASUREMENT, "Pulse Measurement");
        attributes.put(UUID_SA02_MEASUREMENT, "Sa02 Measurement");
        attributes.put(UUID_TIMESTAMP, "Device lifetime time in Miliseconds");
        attributes.put(UUID_CURRENT_TIME, "Current Time");
        attributes.put(UUID_LOCAL_TIME_INFO, "Local Time Information");
        attributes.put(UUID_REFERENCE_TIME_INFO, "Reference Time Information");
        attributes.put(UUID_TIME_UPDATE_CONTROL_POINT, "Time Update Control Point");
        attributes.put(UUID_TIME_UPDATE_STATE, "Time Update State");
        attributes.put(UUID_BATTERY_LEVEL, "Battery level");
        attributes.put(UUID_SILABS_OTA_CONTROL, "Silicon Labs OTA Control");
    }

    public static String getAttributesName(String uuid){
        return attributes.get(uuid);
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
