package com.project.emg_device_client;


import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;

public final class Measurement {
//    private static final Measurement SELF = new Measurement();

    public static ArrayList<Entry> emgData = new ArrayList<Entry>();
    public static ArrayList<Entry> plxData = new ArrayList<Entry>();
    private Measurement() {
        // Don't want anyone else constructing the singleton.
    }

//    public static Measurement getInstance() {
//        return SELF;
//    }
}
