package com.project.emg_device_client;

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
import android.widget.Toast;

import java.util.ArrayList;

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
                //TODO: Save data
            }
        });
        refresh.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(ChartActivity.this, "Reloading...", Toast.LENGTH_LONG).show();
                //TODO: Reload data
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChartActivity.this, "Data removed", Toast.LENGTH_LONG).show();
                // TODO: Remove data
            }
        });
    }
}