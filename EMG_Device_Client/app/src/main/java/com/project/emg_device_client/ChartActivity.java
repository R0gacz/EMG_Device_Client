package com.project.emg_device_client;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.components.LimitLine.LimitLabelPosition;
import com.github.mikephil.charting.utils.Utils;
import android.widget.SeekBar.OnSeekBarChangeListener;
import java.util.ArrayList;
import java.util.List;

public class ChartActivity extends AppCompatActivity{
        private LineChart chart;
        private ArrayList<Entry> data1, data2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.chart_activity);

        chart = findViewById(R.id.chart1);

        data1 = new ArrayList<>();
        data1.add(new Entry(0,1));
        data1.add(new Entry(1,2));
        data1.add(new Entry(2,4));

        data2 = new ArrayList<>();
        data2.add(new Entry(0,3));
        data2.add(new Entry(1,0));
        data2.add(new Entry(2, -3));

        setupChart();
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
}