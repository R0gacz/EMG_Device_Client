package com.project.emg_device_client;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.widget.Toast;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ChartActivity extends AppCompatActivity{
        private LineChart chart;
        private Button save, refresh, remove;
        private ArrayList<Entry> data1, data2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.chart_activity);

        chart = findViewById(R.id.chart1);

        save = findViewById(R.id.save);
        refresh = findViewById(R.id.refresh);
        remove = findViewById(R.id.remove);

        data1 = new ArrayList<>();
        data1.add(new Entry(0,1));
        data1.add(new Entry(1,2));
        data1.add(new Entry(2,4));

        data2 = new ArrayList<>();
        data2.add(new Entry(0,3));
        data2.add(new Entry(1,0));
        data2.add(new Entry(2, -3));

        setupChart();
        setupLiseners();
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
                Toast.makeText(ChartActivity.this, "Data saved", Toast.LENGTH_LONG).show();
                saveData();
            }
        });
        refresh.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(ChartActivity.this, "Reloading...", Toast.LENGTH_LONG).show();
                reloadData();
                refreshChart();
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChartActivity.this, "Data removed", Toast.LENGTH_LONG).show();
                removeData();
                chart.clear();
                chart.invalidate();
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
}