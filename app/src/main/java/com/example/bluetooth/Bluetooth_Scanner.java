package com.example.bluetooth;

        import androidx.appcompat.app.AppCompatActivity;
        import androidx.core.app.ActivityCompat;
        import androidx.core.content.ContextCompat;
        import android.Manifest;
        import android.bluetooth.BluetoothAdapter;
        import android.bluetooth.BluetoothDevice;
        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.content.pm.PackageManager;
        import android.os.Bundle;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.ListView;
        import android.widget.TextView;
        import android.widget.Toast;

        import java.util.ArrayList;


public class Bluetooth_Scanner extends AppCompatActivity {


    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_ACCESS_LOCATION = 11;

    ListView listDevicesFound;
    Button btnScanDevice;
    TextView stateBluetooth;
    BluetoothAdapter bluetoothAdapter;
    ArrayAdapter<String> listAdapter;
    LeDeviceListAdapter leDeviceListAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_scanner);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        registerReceiver(ActionFoundReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        registerReceiver(ActionFoundReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listDevicesFound = (ListView) findViewById(R.id.devicesfound);
        btnScanDevice = (Button) findViewById(R.id.scandevice);
        stateBluetooth = (TextView) findViewById(R.id.bluetoothstate);

        //Adapter
        listAdapter = new ArrayAdapter<String>(Bluetooth_Scanner.this, android.R.layout.simple_list_item_1);
        leDeviceListAdapter = new LeDeviceListAdapter(Bluetooth_Scanner.this, R.layout.list_adapter_view, new ArrayList<>() );
        listDevicesFound.setAdapter(leDeviceListAdapter);

        btnScanDevice.setOnClickListener(view -> onScanDeviceClick());
    }

    private void onScanDeviceClick() {
        registerReceiver(ActionFoundReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));

        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            //check location
            if (checkCoarseLocationPermission()) {
                leDeviceListAdapter.clear();
                bluetoothAdapter.startDiscovery();
            }
        } else {
            checkBlueToothState();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    private boolean checkCoarseLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_LOCATION);
            return false;
        } else {
            return true;
        }
    }

    private void checkBlueToothState() {
        if (bluetoothAdapter == null) {
            stateBluetooth.setText("Bluetooth NOT supported");
        } else {
            if (bluetoothAdapter.isEnabled()) {
                if (bluetoothAdapter.isDiscovering()) {
                    stateBluetooth.setText("Bluetooth is currently in device discovery process.");

                } else {
                    stateBluetooth.setText("Bluetooth is Enabled.");
                    btnScanDevice.setEnabled(true);
                }
            } else {
                stateBluetooth.setText("Bluetooth is NOT Enabled!");
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            onScanDeviceClick();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_ACCESS_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Access corase location allowed, Scan BLE devices", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Access corase location denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    private final BroadcastReceiver ActionFoundReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            intent.setFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            String action = intent.getAction();


           if (BluetoothDevice.ACTION_FOUND.equals(action)) {

               //get Device and rssi
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);


                BTLE_Device adapterDevice = leDeviceListAdapter.getDeviceByAddress(device.getAddress());

                if (adapterDevice == null){
                    adapterDevice = new BTLE_Device(device.getName(), device.getAddress(), rssi);
                    leDeviceListAdapter.add(adapterDevice);
                }else {
                    adapterDevice.setRSSI(rssi);
                }
                leDeviceListAdapter.notifyDataSetChanged();

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                btnScanDevice.setText("Scanning Devices");

            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                btnScanDevice.setText("Scanning in progress...");
            }

        }
    };


}
