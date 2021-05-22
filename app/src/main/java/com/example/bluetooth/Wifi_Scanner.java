package com.example.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Wifi_Scanner extends AppCompatActivity {


    private final static int PERMISSION_REQUEST = 10;
    private static final int REQUEST_ACCESS_LOCATION = 11;
    private WifiManager wifiManager;
    private ListView listView;
    private Button buttonScan;
    private int size = 0;
    private ArrayList<ScanResult> results;
    private WifiAdapter wifiAdapter;
    private static final String TAG= "WIFISCANNER";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_scanner);
        buttonScan = findViewById(R.id.scanWifiBtn);
        listView = findViewById(R.id.wifiList);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        checkWifiState();
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        wifiAdapter = new WifiAdapter(Wifi_Scanner.this, R.layout.list_adapter_view, new ArrayList<>());
        listView.setAdapter(wifiAdapter);

        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkCoarseLocationPermission()) {
                    scanWifi();
                }


            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(wifiReceiver);
    }

    private void scanWifi() {
        //buttonScan.setClickable(false);
        buttonScan.setText("Scanning...");
        boolean success = wifiManager.startScan();
        if (!success) {
            // scan failure handling
            scanFailure();
        }

        Toast.makeText(this, "Scanning WiFi ...", Toast.LENGTH_SHORT).show();
    }


    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);

            if (success) {
                    scanSuccess();

            } else {
                // scan failure handling
                scanFailure();
            }
        }
    };

    public void scanFailure() {
        // handle failure: new scan did NOT succeed
        // consider using old scan results: these are the OLD results!
        results = (ArrayList<ScanResult>) wifiManager.getScanResults();
    }

    private void scanSuccess() {

        results = (ArrayList<ScanResult>) wifiManager.getScanResults();

        for (ScanResult scanResult : results) {

            WifiAP wifiAP = wifiAdapter.getDeviceByAddress(scanResult.BSSID);

            if (wifiAP == null){
                wifiAP = new WifiAP(scanResult.SSID, scanResult.BSSID, scanResult.capabilities,scanResult.level, scanResult.frequency, (String) scanResult.venueName);
                wifiAdapter.add(wifiAP);

            }else {
                wifiAP.setRSSI(scanResult.level);
            }
            wifiAdapter.notifyDataSetChanged();
        }
        unregisterReceiver(wifiReceiver);

        buttonScan.setActivated(true);
        buttonScan.setText("Scan Wifi");
    }


    public boolean checkWifiState() {
        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(this, "WiFi is disabled ... We need to enable it", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_WIFI_STATE}, PERMISSION_REQUEST);
            return false;
        } else {
            return true;
        }
    }

    private boolean checkCoarseLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ACCESS_LOCATION);
            return false;
        } else {
            return true;
        }
    }

}


