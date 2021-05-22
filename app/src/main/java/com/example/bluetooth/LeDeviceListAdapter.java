package com.example.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class LeDeviceListAdapter extends ArrayAdapter<BTLE_Device> {

    private static final String TAG = "LeDeviceListAdapter";
    private Context context;
    int resource;
    private ArrayList<BTLE_Device> devices;


    public LeDeviceListAdapter(Context context, int resource, ArrayList<BTLE_Device> devices) {
        super(context, resource, devices);
        this.context = context;
        this.resource = resource;
        this.devices = devices;
    }

    public void addDevice(BluetoothDevice device, int rssi) {

    }

    public BTLE_Device getDeviceByAddress(String address){
     if (devices != null && !devices.isEmpty()){
         for (BTLE_Device btle_device: devices) {
             if (btle_device.getAddress().equals(address))return btle_device;
         }
     }
     return null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        BTLE_Device device = getItem(position);

        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(resource, parent, false);

        TextView tvName = convertView.findViewById(R.id.deviceName);
        TextView tvAdress = convertView.findViewById(R.id.deviceAdress);
        TextView tvRSSI = convertView.findViewById(R.id.deviceRSSI);
        TextView tvAtrr = convertView.findViewById(R.id.Attribute1);


        tvName.setText(device.getName());

        tvAdress.setText("Mac-Address: " + device.getAddress());
        tvRSSI.setText(String.valueOf("RSSI: " + device.getRSSI() + "dBm"));
        tvAtrr.setText("Rssi in Meter: ~" + device.calculateDistance());

        return convertView;

    }

    public void clear(){
        devices.clear();
    }


}
