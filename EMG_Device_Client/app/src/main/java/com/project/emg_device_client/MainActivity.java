package com.project.emg_device_client;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private LineChart chart;
    private TextView emgDeviceStateTxt;
    private Button save, refresh, remove, startMeasureButton;
    Button addDeviceButton;
    private ArrayList<Entry> sa02Data, pulseData;
    private EmgDeviceService emgDeviceService;
    private EmgDeviceState state;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            emgDeviceService = ((EmgDeviceService.DeviceLocalBinder) service).getService();
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            emgDeviceService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_activity);

        chart = findViewById(R.id.chart1);
        emgDeviceStateTxt = findViewById(R.id.DeviceState);
        state = EmgDeviceState.Disconnected;
        emgDeviceStateTxt.setText(state.toString());

        save = findViewById(R.id.save);
        refresh = findViewById(R.id.refresh);
        remove = findViewById(R.id.remove);
        startMeasureButton = findViewById(R.id.StartMeasureButton);
        addDeviceButton = findViewById(R.id.addDeviceButton);

        sa02Data = new ArrayList<>();
        sa02Data.add(new Entry(0,0));

        pulseData = new ArrayList<>();
        pulseData.add(new Entry(0,0));


        setupChart();
        setupLiseners();

    }

    private void setupChart()
    {
        chart.animateX(3000);
        LineDataSet lineDataSet1 = new LineDataSet(sa02Data, "Set 1");
        lineDataSet1.setColor(Color.CYAN);
        LineDataSet lineDataSet2 = new LineDataSet(pulseData, "Set 2");
        lineDataSet2.setColor(Color.MAGENTA);

        ArrayList<ILineDataSet> dataSet = new ArrayList<>();
        dataSet.add(lineDataSet1);
        dataSet.add(lineDataSet2);

        LineData data = new LineData(dataSet);

        Description description = new Description();
        description.setText("");
        description.setTextSize(25);

        Legend legend = chart.getLegend();

        legend.setTextSize(15);
        legend.setEnabled(true);
        legend.setForm(Legend.LegendForm.LINE);
        legend.setFormSize(15);
        legend.setXEntrySpace(30);

        chart.setDescription(description);
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            // The theme is currently set to dar
            chart.setBackgroundColor(Color.BLACK);
        } else {
            // The theme is currently set to light
            chart.setBackgroundColor(Color.WHITE);
        }
        chart.setNoDataText("No data found");
        chart.setDrawBorders(true);
        chart.setData(data);
        chart.invalidate();

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMinimum(0);
        leftAxis.setAxisMaximum(250);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setAxisMinimum(0);
        rightAxis.setAxisMaximum(100);
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
                if(!startMeasureButton.isSelected()){
                    StartMeasure();
                    startMeasureButton.setSelected(true);
                } else {
                    StopMeasure() ;
                    startMeasureButton.setSelected(false);
                    refreshChart();
                }


            }
        });

        addDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this,
                        ScanForBleDeviceActivity.class), 1);
            }
        });
    }

    private void refreshChart()
    {
        ArrayList<ILineDataSet> dataSet = new ArrayList<>();
        Boolean isUpdated = false;
        if(!sa02Data.isEmpty() )
        {
            LineDataSet lineDataSet1 = new LineDataSet(sa02Data, "Sa02");
            lineDataSet1.setColor(Color.CYAN);
            dataSet.add(lineDataSet1);


            isUpdated = true;
        }
        if(!pulseData.isEmpty())
        {
            LineDataSet lineDataSet2 = new LineDataSet(pulseData, "Pulse");
            lineDataSet2.setColor(Color.MAGENTA);
            dataSet.add(lineDataSet2);

            isUpdated = true;
        }
        if(isUpdated)
        {
            LineData data = new LineData(dataSet);
            chart.animateX(3000);
            chart.setData(data);

            chart.notifyDataSetChanged();
            chart.invalidate();
        }
        else
        {
            Toast.makeText(getApplicationContext(), "No update", Toast.LENGTH_LONG).show();
        }
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
        temp.addAll(sa02Data);
        json_1 = gson.toJson( temp );

        if(sharedPreferences.contains("data2")) {
            json_2 = sharedPreferences.getString("data2", null);
            temp_2 = gson.fromJson(json_2, type);
        }
        temp_2.addAll(pulseData);
        json_2 = gson.toJson( temp_2 );

        editor.putString( "data1", json_1 );
        editor.putString( "data2", json_2 );

        editor.apply();
    }

    public void reloadData()
    {
        Type type = new TypeToken<ArrayList<Entry>>()  {}.getType();

        SharedPreferences sharedPreferences = getSharedPreferences("Data",MODE_PRIVATE);
        Gson gson = new Gson();

        if(sharedPreferences.contains("data1")) {
            String json = sharedPreferences.getString("data1", null);
            sa02Data = gson.fromJson(json, type);
        }
        if(sharedPreferences.contains("data2")) {
            String json_2 = sharedPreferences.getString("data2", null);
            pulseData = gson.fromJson(json_2, type);
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

    private void StartMeasure(){
        if(emgDeviceService != null){
            sa02Data.clear();
            pulseData.clear();
            emgDeviceService.readTimestampValue();
            emgDeviceService.startEmgDataLogging();
            emgDeviceService.startPulseDataLogging();
            emgDeviceService.startSa02DataLogging();
            if (state.equals(EmgDeviceState.Logging)  || state.equals(EmgDeviceState.Connected)) {
                state = EmgDeviceState.Logging;
                emgDeviceStateTxt.setText(state.toString());
            }
        }
        else
        {
            //TODO device disconnected info
        }
    }

    private void StopMeasure(){
        if(emgDeviceService != null){

            emgDeviceService.stopEmgDataLogging();
            emgDeviceService.stopPulseDataLogging();
            emgDeviceService.stopSa02DataLogging();
            if (state.equals(EmgDeviceState.Logging)) {
                state = EmgDeviceState.Connected;
                emgDeviceStateTxt.setText(state.toString());
            }
        }
        else
        {
            //TODO device disconnected info
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void onResume() {
        super.onResume();
        sa02Data.add(new Entry(0,0));
        pulseData.add(new Entry(0,0));
        registerReceiver(dataUpdateReceiver, makeDataUpdateIntentFilter());
        if(EmgDeviceService.MacAddress != null) {
            Intent deviceServiceIntent = new Intent(this, EmgDeviceService.class);
            bindService(deviceServiceIntent, serviceConnection, BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(dataUpdateReceiver);
        if(emgDeviceService != null) {
            unbindService(serviceConnection);
        }

    }

    private final BroadcastReceiver dataUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            byte[] data = intent.getByteArrayExtra(action);
            String name = intent.getStringExtra(action);
            Entry entry;



          switch (action){
                case EmgDeviceService.ACTION_EMG_DATA_RECEIVED:
//                    entry = emgDeviceService.EmgDataBytesToEntry(data);
//                    emgData.add(entry);
//                    refreshChart();
                    break;
              case EmgDeviceService.ACTION_PULSE_DATA_RECEIVED:
                    entry = emgDeviceService.PulseDataBytesToEntry(data);
                    pulseData.add(entry);
                    refreshChart();
                    break;
              case EmgDeviceService.ACTION_SA02_DATA_RECEIVED:
                    entry = emgDeviceService.Sa02DataBytesToEntry(data);
                    sa02Data.add(entry);
                    refreshChart();
                    break;
              case EmgDeviceService.ACTION_DEVICE_CONNECTION_CHANGED:
                    state = EmgDeviceState.valueOf(name);
                    emgDeviceStateTxt.setText(name);
                    break;
              default:
                    break;

            }
        }
    };

    private static IntentFilter makeDataUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(EmgDeviceService.ACTION_EMG_DATA_RECEIVED);
        intentFilter.addAction(EmgDeviceService.ACTION_PULSE_DATA_RECEIVED);
        intentFilter.addAction(EmgDeviceService.ACTION_SA02_DATA_RECEIVED);
        intentFilter.addAction(EmgDeviceService.ACTION_DEVICE_CONNECTION_CHANGED);
        return intentFilter;
    }
}