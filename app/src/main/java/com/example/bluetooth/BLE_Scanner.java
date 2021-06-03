package com.example.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;


import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;

public class BLE_Scanner extends AppCompatActivity {

    private static final String TAG = "BLE_Scanner";
    private ListView listView;
    private ToggleButton buttonScan;
    private BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
    //private BluetoothLeScanner scanner = adapter.getBluetoothLeScanner();
    private static final int REQUEST_ACCESS_LOCATION = 11;
    private BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();


    //Adapter
    LeDeviceListAdapter leDeviceListAdapter;


    private JSONArray jsonArray;
    private ArrayList<ArrayList<ArrayList<String>>> outputArray = new ArrayList();
    private final ArrayList<ArrayList<String>> inputArray = new ArrayList<>();
    //private String[][] macArray = new String[][];
    JSONObject jo = new JSONObject();
    private String scanIteration = "Scan ";
    int scanIterator=0;



    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.ble_scanner);
        buttonScan = findViewById(R.id.scanBLEBtn);
        listView = findViewById(R.id.blelist);

        //Adapter
        leDeviceListAdapter = new LeDeviceListAdapter(BLE_Scanner.this, R.layout.list_adapter_view, new ArrayList<>() );
        listView.setAdapter(leDeviceListAdapter);

        buttonScan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    if (checkCoarseLocationPermission()) {
                        scanIterator++;
                        scanBLE();
                    }
                } else {
                    // The toggle is disabled
                    scanner.stopScan(scanCallback);
                    saveToExternalStorage();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanner.stopScan(scanCallback);
    }

    private void scanBLE() {

        if (scanner != null) {
            scanner.startScan(null, scanSettings, scanCallback);
            Log.d(TAG, "scan started");
        }  else {
            Log.e(TAG, "could not get scanner object");
        }
    }


    private final ScanCallback scanCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {

            BluetoothDevice device = result.getDevice();
            // ...do whatever you want with this found device
            BTLE_Device adapterDevice = leDeviceListAdapter.getDeviceByAddress(device.getAddress());

            if (adapterDevice == null){
                adapterDevice = new BTLE_Device(device.getName(), device.getAddress(), result.getRssi());
                leDeviceListAdapter.add(adapterDevice);
            }else {
                adapterDevice.setRSSI(result.getRssi());
            }
            leDeviceListAdapter.notifyDataSetChanged();

        }


        @Override
        public void onBatchScanResults(@NonNull List<ScanResult> results) {



            for (ScanResult result: results) {
                BluetoothDevice device = result.getDevice();
                // ...do whatever you want with this found device
                BTLE_Device adapterDevice = leDeviceListAdapter.getDeviceByAddress(device.getAddress());

                if (adapterDevice == null) {
                    adapterDevice = new BTLE_Device(device.getName(), device.getAddress(), result.getRssi());
                    leDeviceListAdapter.add(adapterDevice);
                } else {
                    adapterDevice.setRSSI(result.getRssi());
                }
                leDeviceListAdapter.notifyDataSetChanged();


                ArrayList<Integer> rssi = new ArrayList<>();
                rssi.add(result.getRssi());


                try {
                    jo.accumulate(device.getAddress(),result.getRssi());
                    //jo.accumulate(device.getAddress(), result.getRssi());
                    //if (device.getName() == null){
                       // jo.put("NoName", result.getTxPower());
                    //}else{
                   //     jo.put(device.getName(), result.getTxPower());
                   // }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                System.out.println(jo);

            }

        }


        @Override
        public void onScanFailed(int errorCode) {
            // Ignore for now
        }
    };

    /*
    ScanSettings scanSettings = new ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)

            .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
            .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
            .setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT)
            .setReportDelay(6000)
            .build();

     */

    ScanSettings  scanSettings = new ScanSettings.Builder()
            .setLegacy(false)
            .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
            .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
            .setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT)
            .setReportDelay(2000)
            .setUseHardwareBatchingIfSupported(true)
            .build();




    private boolean checkCoarseLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ACCESS_LOCATION);
            return false;
        } else {
            return true;
        }
    }


    private void saveToExternalStorage() {
        String FILENAME = scanIteration + scanIterator + ".json";
        String stringArray = jo.toString();
        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Bluetooth");
        if (!folder.exists())folder.mkdir();
        File myFile = new File(folder, FILENAME);
        FileOutputStream fstream = null;
        try {
            fstream = new FileOutputStream(myFile);
            fstream.write(stringArray.getBytes());
            fstream.close();
            Toast.makeText(this, "File: " + FILENAME + " saved in Gesture Folder", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
