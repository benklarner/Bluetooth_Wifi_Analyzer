package com.example.bluetooth;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class WifiAdapter extends ArrayAdapter<WifiAP> {

    ArrayList<WifiAP> scanResults;
    private Context context;
    int resource;


    public WifiAdapter(Context context, int resource, ArrayList<WifiAP> scanResults) {
        super(context, resource, scanResults);

        this.context = context;
        this.resource = resource;
        this.scanResults = scanResults;
    }


    public WifiAP getDeviceByAddress(String address) {

        //Log.d("WIFIADAPTER", address);

        if (scanResults != null && !scanResults.isEmpty()) {
            for (WifiAP wifiAP : scanResults) {
                if (wifiAP.BSSID.equals(address)) return wifiAP;
            }
        }
        return null;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        WifiAP scanResult = getItem(position);

        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(resource, parent, false);

        TextView tvName = convertView.findViewById(R.id.deviceName);
        TextView tvAdress = convertView.findViewById(R.id.deviceAdress);
        TextView tvRSSI = convertView.findViewById(R.id.deviceRSSI);
        TextView tvAttrb1 = convertView.findViewById(R.id.Attribute1);
        TextView tvAttrb2 = convertView.findViewById(R.id.Attribute2);


        tvName.setText(scanResult.getSSID());
        tvAdress.setText("Mac-Address: " + scanResult.getBSSID());
        tvRSSI.setText(String.valueOf("RSSI: " + scanResult.getRSSI() + "dBm"));
        tvAttrb1.setText(scanResult.getCapabilities());
        tvAttrb2.setText("Strength in m " + "~" + calculateDistance(scanResult.getRSSI(), scanResult.getFrequency()) + "m");

        return convertView;

    }

    public void clear(
    ) {
        scanResults.clear();
    }

    public double calculateDistance(double signalLevelInDb, double freqInMHz) {
        double result;
        double exp = (27.55 - (20 * Math.log10(freqInMHz)) + Math.abs(signalLevelInDb)) / 20.0;
        result = Math.round(100.0 * (Math.pow(10.0, exp))) / 100.0;
        return result;
    }

}
