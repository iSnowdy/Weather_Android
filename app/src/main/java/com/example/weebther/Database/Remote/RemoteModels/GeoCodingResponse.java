package com.example.weebther.Database.Remote.RemoteModels;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

// Response from the Nominatim API

public class GeoCodingResponse {
    @SerializedName("lat")
    private String lat;
    @SerializedName("lon")
    private String lon;
    @SerializedName("display_name")
    private String displayName;

    public double getLatitude() {
        return Double.parseDouble(lat);
    }

    public double getLongitude() {
        return Double.parseDouble(lon);
    }

    public String getCountry() {
        String[] parts = displayName.split(",");
        if (parts.length == 0) return "Unknown";
        return parts[parts.length - 1].trim();
    }
}
