package com.example.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

public class BTLE_Device {

    private BluetoothDevice bluetoothDevice;
    private int rssi;
    String name,address;

    public BTLE_Device(String name,String address, int rssi) {
        this.bluetoothDevice = bluetoothDevice;
        this.rssi  = rssi;
        this.name=name;
        this.address=address;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public void setRSSI(int rssi) {
        this.rssi = rssi;
    }

    public int getRSSI() {
        return rssi;
    }



    public double calculateDistance() {

        double txPower = -59; //hard coded power value. Usually ranges between -59 to -65

        if (rssi == 0) {
            return -1.0;
        }

        double ratio = rssi*1.0/txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio,10);
        }
        else {
            double distance =  (0.89976)*Math.pow(ratio,7.7095) + 0.111;
            return Math.round(distance);

        }
    }
}
