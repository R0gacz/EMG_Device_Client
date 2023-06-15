package com.project.emg_device_client;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LineChart chart;
    private Button save, refresh, remove, startMeasureButton;
    FloatingActionButton addDeviceButton;
    private ArrayList<Entry> data1, data2;
    private BluetoothLeService bleLeService;
    private String connectedDeviceAddres;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            bleLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!bleLeService.initialize()) {
                return;
            }
            // Automatically connects to the device upon successful start-up initialization.
            bleLeService.connect(connectedDeviceAddres);
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bleLeService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_activity);

        chart = findViewById(R.id.chart1);

        save = findViewById(R.id.save);
        refresh = findViewById(R.id.refresh);
        remove = findViewById(R.id.remove);
        startMeasureButton = findViewById(R.id.StartMeasureButton);
        addDeviceButton = (FloatingActionButton) findViewById(R.id.addDeviceButton);
        bleLeService = null;
        connectedDeviceAddres = null;

        data1 = new ArrayList<>();
        data1.add(new Entry(0,1));
        data1.add(new Entry(1,2));
        data1.add(new Entry(2,4));
        data1.add(new Entry(3, 2));
        data1.add(new Entry(4, 2));

        data2 = new ArrayList<>();
        data2.add(new Entry(0,3));
        data2.add(new Entry(1,0));
        data2.add(new Entry(2, -3));

        setupChart();
        setupLiseners();

        addDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this,
                        ScanForBleDeviceActivity.class), 1);
            }
        });

    }

    private void setupChart()
    {
        chart.animateX(3000);
        LineDataSet lineDataSet1 = new LineDataSet(data1, "Set 1");
        lineDataSet1.setColor(Color.CYAN);
        LineDataSet lineDataSet2 = new LineDataSet(data2, "Set 2");
        lineDataSet2.setColor(Color.MAGENTA);

        ArrayList<ILineDataSet> dataSet = new ArrayList<>();
        dataSet.add(lineDataSet1);
        dataSet.add(lineDataSet2);

        LineData data = new LineData(dataSet);

        Description description = new Description();
        description.setText("Activity chart");
        description.setTextSize(25);

        Legend legend = chart.getLegend();

        legend.setTextSize(15);
        legend.setEnabled(true);
        legend.setForm(Legend.LegendForm.LINE);
        legend.setFormSize(15);
        legend.setXEntrySpace(30);

        chart.setDescription(description);
        chart.setBackgroundColor(Color.WHITE);
        chart.setNoDataText("No data found");
        chart.setDrawBorders(true);
        chart.setData(data);
        chart.invalidate();
    }

    private void setupLiseners()
    {
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Data saved", Toast.LENGTH_LONG).show();
                saveData();
            }
        });
        refresh.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Reloading...", Toast.LENGTH_LONG).show();
                reloadData();
                refreshChart();
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Data removed", Toast.LENGTH_LONG).show();
                removeData();
                chart.clear();
                chart.invalidate();
            }
        });


        startMeasureButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startMeasure();
            }
        });
    }

    private void refreshChart()
    {
        LineDataSet lineDataSet1 = new LineDataSet(data1, "Set 1");
        lineDataSet1.setColor(Color.CYAN);
        LineDataSet lineDataSet2 = new LineDataSet(data2, "Set 2");
        lineDataSet2.setColor(Color.MAGENTA);

        ArrayList<ILineDataSet> dataSet = new ArrayList<>();
        dataSet.add(lineDataSet1);
        dataSet.add(lineDataSet2);

        LineData data = new LineData(dataSet);

        chart.setData(data);

        chart.notifyDataSetChanged();
        chart.invalidate();
    }

    private void saveData()
    {
        Type type = new TypeToken<ArrayList<Entry>>() {}.getType();
        String json_1, json_2;
        ArrayList<Entry> temp = new ArrayList<>();
        ArrayList<Entry> temp_2 = new ArrayList<>();

        SharedPreferences sharedPreferences = getSharedPreferences("Data",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        if(sharedPreferences.contains("data1")) {
            json_1 = sharedPreferences.getString("data1", null);
            temp = gson.fromJson(json_1, type);
        }
        temp.addAll( data1 );
        json_1 = gson.toJson( temp );

        if(sharedPreferences.contains("data2")) {
            json_2 = sharedPreferences.getString("data2", null);
            temp_2 = gson.fromJson(json_2, type);
        }
        temp_2.addAll( data2 );
        json_2 = gson.toJson( temp_2 );

        editor.putString( "data1", json_1 );
        editor.putString( "data2", json_2 );

        editor.apply();
    }

    public void reloadData()
    {
        Type type = new TypeToken<List<String>>() {}.getType();

        SharedPreferences sharedPreferences = getSharedPreferences("Data",MODE_PRIVATE);
        Gson gson = new Gson();

        if(sharedPreferences.contains("data1")) {
            String json = sharedPreferences.getString("data1", null);
            data1 = gson.fromJson(json, type);
        }
        if(sharedPreferences.contains("data2")) {
            String json_2 = sharedPreferences.getString("data2", null);
            data2 = gson.fromJson(json_2, type);
        }
    }

    private void removeData()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("Data",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.remove("data1");
        editor.remove( "data2" );

        editor.apply();
    }

    public void startMeasure(){
        if (bleLeService == null) return;
        BluetoothGattCharacteristic currTime = bleLeService.getCharacteristicByUuid(GattAttributes.UUID_CURRENT_TIME_SERVICE, GattAttributes.UUID_CURRENT_TIME);
        BluetoothGattCharacteristic emgMeasurment = bleLeService.getCharacteristicByUuid(GattAttributes.UUID_TIMESTAMPED_DATA_SERVICE, GattAttributes.UUID_EMG_MEASUREMENT);
        BluetoothGattCharacteristic plxMeasurment = bleLeService.getCharacteristicByUuid(GattAttributes.UUID_TIMESTAMPED_DATA_SERVICE, GattAttributes.UUID_PLX_MEASUREMENT);

        if (currTime != null) bleLeService.setCharacteristicNotification(currTime,true);
        if (emgMeasurment != null) bleLeService.setCharacteristicNotification(emgMeasurment,true);
        if (plxMeasurment != null) bleLeService.setCharacteristicNotification(plxMeasurment,true);
    }

    private void addValueToMeasureArray(String characteristicUuid, byte[] data){

        int timestamp = 0;
        int value = 0;

        int length = data.length;

        for (int i = 0; i < 8; i++) {
            data[length - i - 1] = (byte) (timestamp & 0xFF);
            timestamp >>= 8;
        }

        if(characteristicUuid.equals(GattAttributes.UUID_EMG_MEASUREMENT)) data1.add(new Entry(timestamp, value));
        if(characteristicUuid.equals(GattAttributes.UUID_PLX_MEASUREMENT)) data2.add(new Entry(timestamp, value));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
//            if (resultCode == Activity.RESULT_OK) {

//                String addres = data.("connectedBleDevice");  TODO get data as null
                connectedDeviceAddres = "58:8E:81:A5:46:9A";
                if(connectedDeviceAddres == null) return;
                Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
                bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
//            }
        }
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
//                mConnected = true;
//                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
//                mConnected = false;
//                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
//            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
//                // Show all the supported services and characteristics on the user interface.
//                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                String[] data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA).split("\n");
                addValueToMeasureArray(data[0], data[1].getBytes());
            }
        }
    };

    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (bleLeService != null) {
            final boolean result = bleLeService.connect(connectedDeviceAddres);
            Log.d(TAG, "Connect request result=" + result);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

}