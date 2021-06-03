package com.example.bluetooth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE_BLUETOOTH = 14;
    private static final int PERMISSION_REQUEST_CODE_WIFI = 15;
    private final int STORAGE_PERMISSION_CODE = 1;

    private Button blScanner, wifiScanner, bleScanner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        blScanner = findViewById(R.id.bluetoothscanner);
        wifiScanner = findViewById(R.id.wifiscanner);
        bleScanner = findViewById(R.id.blescanner);

        blScanner.setOnClickListener(v -> checkPermissionAndStartActivity(Bluetooth_Scanner.class,
                PERMISSION_REQUEST_CODE_BLUETOOTH));
        wifiScanner.setOnClickListener(v -> checkPermissionAndStartActivity(Wifi_Scanner.class,
                PERMISSION_REQUEST_CODE_WIFI));
        bleScanner.setOnClickListener(v -> checkPermissionAndStartActivity(BLE_Scanner.class,
                PERMISSION_REQUEST_CODE_BLUETOOTH));

    }

    private void checkPermissionAndStartActivity(Class<? extends Activity> activityToStart, int code) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE}, code);
        } else if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, code);
        } else {
            startActivity(new Intent(MainActivity.this, activityToStart));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE_BLUETOOTH)
            checkPermissionAndStartActivity(Bluetooth_Scanner.class, PERMISSION_REQUEST_CODE_BLUETOOTH);

        if (requestCode == PERMISSION_REQUEST_CODE_WIFI)
            checkPermissionAndStartActivity(Bluetooth_Scanner.class, PERMISSION_REQUEST_CODE_WIFI);
    }
}