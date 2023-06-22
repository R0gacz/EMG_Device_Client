package com.project.emg_device_client;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static com.project.emg_device_client.BluetoothLeService.EXTRA_CHARACTERISTIC_UUID;

import android.app.Service;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.github.mikephil.charting.data.Entry;


public class EmgDeviceService extends Service {

    public static String MacAddress;

    private Binder binder = new DeviceLocalBinder();
    private BluetoothLeService bleLeService;
    private long startMeasureTimeInMs;
    private Boolean firstTimeValueFlag;

    public final static String ACTION_EMG_DATA_RECEIVED =
            "EmgDeviceService.ACTION_EMG_DATA_RECEIVED";
    public final static String ACTION_PULSE_DATA_RECEIVED =
            "EmgDeviceService.ACTION_PULSE_DATA_RECEIVED";
    public final static String ACTION_SA02_DATA_RECEIVED =
            "EmgDeviceService.ACTION_SA02_DATA_RECEIVED";
    public final static String ACTION_DEVICE_CONNECTION_CHANGED =
            "EmgDeviceService.ACTION_DEVICE_CONNECTION_CHANGED";

    class DeviceLocalBinder extends Binder {
        public EmgDeviceService getService() {
            return EmgDeviceService.this;
        }
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            bleLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!bleLeService.initialize()) {
                return;
            }
            bleLeService.connect(MacAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

            bleLeService = null;
        }
    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (bleLeService != null) {
            final boolean result = bleLeService.connect(MacAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        startMeasureTimeInMs = 0;

        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public boolean onUnbind(Intent intent) {
        unbindService(mServiceConnection);
        unregisterReceiver(mGattUpdateReceiver);
        return super.onUnbind(intent);
    }


    public void startEmgDataLogging(){
        startDataLogging(GattAttributes.UUID_TIMESTAMPED_DATA_SERVICE, GattAttributes.UUID_EMG_MEASUREMENT);
    }

    public void startPulseDataLogging(){
        startDataLogging(GattAttributes.UUID_TIMESTAMPED_DATA_SERVICE, GattAttributes.UUID_PULSE_MEASUREMENT);
    }

    public void startSa02DataLogging(){
        startDataLogging(GattAttributes.UUID_TIMESTAMPED_DATA_SERVICE, GattAttributes.UUID_SA02_MEASUREMENT);
    }

    public void stopEmgDataLogging(){
        stopDataLogging(GattAttributes.UUID_TIMESTAMPED_DATA_SERVICE, GattAttributes.UUID_EMG_MEASUREMENT);
    }

    public void stopPulseDataLogging(){
        stopDataLogging(GattAttributes.UUID_TIMESTAMPED_DATA_SERVICE, GattAttributes.UUID_PULSE_MEASUREMENT);
    }

    public void stopSa02DataLogging(){
        stopDataLogging(GattAttributes.UUID_TIMESTAMPED_DATA_SERVICE, GattAttributes.UUID_SA02_MEASUREMENT);
    }

    public void readTimestampValue(){
        if (bleLeService == null) return;
        BluetoothGattCharacteristic characteristic = bleLeService.getCharacteristicByUuid(GattAttributes.UUID_TIMESTAMPED_DATA_SERVICE, GattAttributes.UUID_TIMESTAMP);
        if (characteristic != null){
            bleLeService.setCharacteristicNotification(characteristic, false);
            bleLeService.readCharacteristic(characteristic);
            firstTimeValueFlag = true;
        }
    }


    private void startDataLogging(String serviceUuid, String characteristicUuid) {
        if (bleLeService == null) return;
        BluetoothGattCharacteristic characteristic = bleLeService.getCharacteristicByUuid(serviceUuid, characteristicUuid);
        if (characteristic != null) bleLeService.setCharacteristicNotification(characteristic, true);
    }

    private void stopDataLogging(String serviceUuid, String characteristicUuid) {
        if (bleLeService == null) return;
        BluetoothGattCharacteristic characteristic = bleLeService.getCharacteristicByUuid(serviceUuid, characteristicUuid);
        if (characteristic != null) bleLeService.setCharacteristicNotification(characteristic, false);
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                connectionStateUpdate(EmgDeviceState.Connecting);
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                connectionStateUpdate(EmgDeviceState.Disconnected);
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                connectionStateUpdate(EmgDeviceState.Connected);
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                String characteristicUuid = intent.getStringExtra(EXTRA_CHARACTERISTIC_UUID);
                byte[] data = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                characteristicValueUpdate(characteristicUuid, data);
            }
        }
    };

    void characteristicValueUpdate(String characteristicUuid, byte[] data)
    {
        switch (characteristicUuid){
            case GattAttributes.UUID_EMG_MEASUREMENT:
                broadcastUpdate(ACTION_EMG_DATA_RECEIVED,  data);
                break;
            case GattAttributes.UUID_PULSE_MEASUREMENT:
                broadcastUpdate(ACTION_PULSE_DATA_RECEIVED,  data);
                break;
            case GattAttributes.UUID_SA02_MEASUREMENT:
                broadcastUpdate(ACTION_SA02_DATA_RECEIVED,  data);
                break;
            case GattAttributes.UUID_TIMESTAMP:
                if(firstTimeValueFlag) {
                    startMeasureTimeInMs = (data[0] & 0xFF) << 24 |
                        (data[1] & 0xFF) << 16 |
                        (data[2] & 0xFF) << 8 |
                        (data[3] & 0xFF);
                    firstTimeValueFlag = false;
                }
                break;
            default:
                break;
        }
    }

    private void connectionStateUpdate(EmgDeviceState state) {

        final Intent intent = new Intent(ACTION_DEVICE_CONNECTION_CHANGED);
        intent.putExtra(ACTION_DEVICE_CONNECTION_CHANGED, state.name());
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,
                                 final byte[] data) {

        final Intent intent = new Intent(action);
        intent.putExtra(action, data);
        sendBroadcast(intent);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    public Entry EmgDataBytesToEntry(byte[] data){

        int timestamp = 0;
        int value = 0;

        int length = data.length;

//        TODO impl data parser for emg

        return new Entry(timestamp, value);
    }

    public Entry PulseDataBytesToEntry(byte[] data){

        long timestamp = 0;
        int value = 0;

        int length = data.length;

        value = (data[0] & 0xFF) << 24 |
                (data[1] & 0xFF) << 16 |
                (data[2] & 0xFF) << 8 |
                (data[3] & 0xFF);

        int ms = (data[4] & 0xFF) << 24 |
                (data[5] & 0xFF) << 16 |
                (data[6] & 0xFF) << 8 |
                (data[7] & 0xFF);

        timestamp = (ms - startMeasureTimeInMs)/1000;

        return new Entry(timestamp, value);
    }

    public Entry Sa02DataBytesToEntry(byte[] data){

        long timestamp = 0;
        int value = 0;

        int length = data.length;

        value = (data[0] & 0xFF) << 24 |
                (data[1] & 0xFF) << 16 |
                (data[2] & 0xFF) << 8 |
                (data[3] & 0xFF);

        int ms = (data[4] & 0xFF) << 24 |
                (data[5] & 0xFF) << 16 |
                (data[6] & 0xFF) << 8 |
                (data[7] & 0xFF);

        timestamp = (ms - startMeasureTimeInMs)/1000;

        return new Entry(timestamp, value);
    }
}
