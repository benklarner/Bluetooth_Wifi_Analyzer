package com.example.bluetooth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    Button blScanner, wifiScanner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        blScanner = findViewById(R.id.blescanner);
        wifiScanner = findViewById(R.id.wifiscanner);

        blScanner.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Bluetooth_Scanner.class)));
        wifiScanner.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Wifi_Scanner.class)));


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 14);
        }
    }
}