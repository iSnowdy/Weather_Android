package Domain.Models;

import com.google.gson.annotations.SerializedName;

// Response from the Nominatim API

public class GeoCodingResponse {
    @SerializedName("lat")
    private String lat;
    @SerializedName("lon")
    private String lon;

    public double getLatitude() {
        return Double.parseDouble(lat);
    }

    public double getLongitude() {
        return Double.parseDouble(lon);
    }
}
