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

import java.util.Calendar;

public class EmgDeviceService extends Service {

    public static String MacAddress;

    private Binder binder = new DeviceLocalBinder();
    private BluetoothLeService bleLeService;

    public final static String ACTION_EMG_DATA_RECEIVED =
            "EmgDeviceService.ACTION_EMG_DATA_RECEIVED";
    public final static String ACTION_PLX_DATA_RECEIVED =
            "EmgDeviceService.ACTION_PLX_DATA_RECEIVED";
    public final static String ACTION_TIME_DATA_RECEIVED =
            "EmgDeviceService.ACTION_TIME_DATA_RECEIVED";

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

    public void startPlxDataLogging(){
        startDataLogging(GattAttributes.UUID_TIMESTAMPED_DATA_SERVICE, GattAttributes.UUID_PLX_MEASUREMENT);
    }

    public void startTimeDataLogging(){
        startDataLogging(GattAttributes.UUID_CURRENT_TIME_SERVICE, GattAttributes.UUID_CURRENT_TIME);
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

    private boolean _connected;

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
//                _connected = true;
//                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
//                _connected = false;
//                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
//                displayGattServices(mBluetoothLeService.getSupportedGattServices());
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
            case GattAttributes.UUID_PLX_MEASUREMENT:
                broadcastUpdate(ACTION_PLX_DATA_RECEIVED,  data);
                break;
            case GattAttributes.UUID_CURRENT_TIME:
                broadcastUpdate(ACTION_TIME_DATA_RECEIVED,  data);
                break;
            default:
//                broadcastUpdate(ACTION_EMG_DATA_RECEIVED,  characteristicUuid);
                break;
        }
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

        for (int i = 0; i < 8; i++) {
            data[length - i - 1] = (byte) (timestamp & 0xFF);
            timestamp >>= 8;
        }

        long ms = Calendar.getInstance().getTimeInMillis();
        return new Entry(ms, value);
    }

    public Entry PlxDataBytesToEntry(byte[] data){

        int timestamp = 0;
        int value = 0;

        int length = data.length;

        for (int i = 0; i < 8; i++) {
            data[length - i - 1] = (byte) (timestamp & 0xFF);
            timestamp >>= 8;
        }

        long ms = Calendar.getInstance().getTimeInMillis();
        return new Entry(ms, value);
    }
}
