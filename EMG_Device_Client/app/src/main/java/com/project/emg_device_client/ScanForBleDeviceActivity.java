package com.project.emg_device_client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Arrays;


public class ScanForBleDeviceActivity extends AppCompatActivity {

    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    BluetoothLeScanner btScanner;
    Button startScanningButton;
    RecyclerView recyclerAvailableDevices;
    RecycleAvailableDeviceAdapter availableDeviceadapter;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final int PERMISSION_REQUEST_FINE_LOCATION = 2;
    private static final int PERMISSION_REQUEST_BLUETOOTH_ADMIN = 3;
    private static final int PERMISSION_REQUEST_BLUETOOTH_SCAN = 4;
    private static final int PERMISSION_REQUEST_BLUETOOTH_CONNECT = 5;

    ArrayList<AvailableDevice> arrAvailebleDev = new ArrayList<AvailableDevice>();

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_for_ble_device);

        recyclerAvailableDevices = findViewById(R.id.recyclerAvailableDevices);
        recyclerAvailableDevices.setLayoutManager(new LinearLayoutManager(this));

        availableDeviceadapter = new RecycleAvailableDeviceAdapter(this, arrAvailebleDev);
        recyclerAvailableDevices.setAdapter(availableDeviceadapter);

        startScanningButton = (Button) findViewById(R.id.StartScanButton);
        startScanningButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                changeScanningStatus();
            }
        });


        btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        btScanner = btAdapter.getBluetoothLeScanner();


        if (btAdapter != null && !btAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }

        // Make sure we have required permission granted, if not, prompt the user to enable it
        PermissionRequestLauncher();

    }

    // Device scan callback.
    private ScanCallback leScanCallback = new ScanCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            boolean alreadyPresent = false;
            for (AvailableDevice device : arrAvailebleDev){
                if(device.macAddress.equals(result.getDevice().getAddress())){
                    alreadyPresent = true;
                    break;
                }
                else {
                    device.rssi = result.getRssi();
                    device.name = result.getDevice().getName();
                }
            }
            if (!alreadyPresent) {
                arrAvailebleDev.add(new AvailableDevice(result));
            }
            availableDeviceadapter.notifyDataSetChanged();
        }
    };

    private void PermissionRequestLauncher() {
        if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
        }

        if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_FINE_LOCATION);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, PERMISSION_REQUEST_BLUETOOTH_CONNECT);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.BLUETOOTH_SCAN}, PERMISSION_REQUEST_BLUETOOTH_SCAN);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.BLUETOOTH_ADMIN}, PERMISSION_REQUEST_BLUETOOTH_ADMIN);
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("Coarse location permission granted");
                } else {
                    ShowLimitedFunctionalityAlertDialogAndFinishActivity("Coarse location");
                }
                return;
            }
            case PERMISSION_REQUEST_FINE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("Fine location permission granted");
                } else {
                    ShowLimitedFunctionalityAlertDialogAndFinishActivity("Fine location");
                }
                return;
            }
            case PERMISSION_REQUEST_BLUETOOTH_CONNECT: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("Bluetooth connect permission granted");
                } else {
                    ShowLimitedFunctionalityAlertDialogAndFinishActivity("Bluetooth connect");
                }
                return;
            }
            case PERMISSION_REQUEST_BLUETOOTH_SCAN: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("Bluetooth scan permission granted");
                } else {
                    ShowLimitedFunctionalityAlertDialogAndFinishActivity("Bluetooth scan");
                }
                return;
            }
            case PERMISSION_REQUEST_BLUETOOTH_ADMIN: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("Bluetooth admin permission granted");
                } else {
                    ShowLimitedFunctionalityAlertDialogAndFinishActivity("Bluetooth admin");
                }
                return;
            }




        }
    }
    private void ShowLimitedFunctionalityAlertDialogAndFinishActivity(String permissionName){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Functionality limited");
//        builder.setMessage("Since permission has not been granted, adding a new device will not be possible.");
        builder.setMessage("Since " + permissionName + " permission has not been granted, adding a new device will not be possible.");
        builder.setPositiveButton(android.R.string.ok, null);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            finish();
        }
    });
        builder.show();
}

    public void changeScanningStatus() {
        if (!startScanningButton.isSelected()) {
            arrAvailebleDev.clear();
            System.out.println("start scanning");
            startScanningButton.setText("STOP SCAN");
            startScanningButton.setSelected(true);
            AsyncTask.execute(new Runnable() {
                @SuppressLint("MissingPermission")
                @Override
                public void run() {
                    btScanner.startScan(leScanCallback);
                }
            });
        } else {
            System.out.println("stopping scanning");
            startScanningButton.setText("START SCAN");
            startScanningButton.setSelected(false);
            AsyncTask.execute(new Runnable() {
                @SuppressLint("MissingPermission")
                @Override
                public void run() {
                    btScanner.stopScan(leScanCallback);
                }

            });
        }
    }
}
