package com.example.weebther.Database.Remote;

import com.example.weebther.Exceptions.GeoLocatorException;

public interface GeoCodingCallBack {
    void onSuccess(double lat, double lon);
    void onError(GeoLocatorException geoLocatorException, Throwable throwable);
}
