package Database.Remote;

import Exceptions.GeoLocatorException;

public interface GeoCodingCallBack {
    void onSuccess(double lat, double lon);
    void onError(GeoLocatorException geoLocatorException, Throwable throwable);
}
