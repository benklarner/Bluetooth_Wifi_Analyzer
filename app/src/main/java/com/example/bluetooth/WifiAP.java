package com.example.bluetooth;

public class WifiAP {

    String SSID,BSSID, capabilities, venuname;
    int RSSI, frequency;

    public WifiAP(String SSID, String BSSID, String capabilities, int RSSI, int frequency, String venuname) {
        this.SSID = SSID;
        this.BSSID = BSSID;
        this.capabilities = capabilities;
        this.RSSI = RSSI;
        this.frequency=frequency;
        this.venuname= venuname;
    }

    public String getSSID() {
        return SSID;
    }

    public String getBSSID() {
        return BSSID;
    }


    public String getCapabilities() {
        return capabilities;
    }

    public int getRSSI() {
        return RSSI;
    }

    public void setRSSI(int RSSI) {
        this.RSSI = RSSI;
    }

    public String getVenuname() {
        return venuname;
    }

    public int getFrequency() {
        return frequency;
    }
}
