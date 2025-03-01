package com.example.weebther.Database.Remote;

import com.example.weebther.Database.Local.Entity.City;
import com.example.weebther.Exceptions.GeoLocatorException;

public interface GeoCodingCallBack {
    void onSuccess(City city);
    void onError(GeoLocatorException geoLocatorException, Throwable throwable);
}
