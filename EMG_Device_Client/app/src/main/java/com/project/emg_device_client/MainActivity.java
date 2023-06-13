package com.project.emg_device_client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ComponentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton addDeviceButton = (FloatingActionButton) findViewById(R.id.addDeviceButton);

//        if (getApplicationContext().getPackageManager().hasSystemFeature(
//                PackageManager.FEATURE_BLUETOOTH_LE)) {
//            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
//            };
//        } else {
//            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle("No BLE available");
//            builder.setMessage("Unfortunately your device does not have the required functionality. App is closing now.");
//            builder.setPositiveButton(android.R.string.ok, null);
//            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                @Override
//                public void onDismiss(DialogInterface dialog) {
//                    finishAffinity();
//                }
//            });
//            builder.show();
//        }


        addDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ScanForBleDeviceActivity.class));
            }
        });
    }
}