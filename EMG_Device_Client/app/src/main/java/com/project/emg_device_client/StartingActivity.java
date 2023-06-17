package com.project.emg_device_client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class StartingActivity extends AppCompatActivity {
    Button startButton, infoButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);

        startButton = findViewById(R.id.startButton);
        infoButton = findViewById(R.id.infoButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(StartingActivity.this,
                        MainActivity.class), 1);
            }
        });

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Application made by R0gacz & AC", Toast.LENGTH_LONG).show();
            }
        });
    }
}