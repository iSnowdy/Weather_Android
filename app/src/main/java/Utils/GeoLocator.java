package Utils;



import android.os.Looper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Handler;

import javax.net.ssl.HttpsURLConnection;

import Exceptions.GeoLocatorException;
import okhttp3.OkHttpClient;

public class GeoLocator {
    private static final String API_URL = "https://nominatim.openstreetmap.org/search?q=%s&format=json&limit=1";
    private static final OkHttpClient client = new OkHttpClient();
    private static final Handler handler = new Handler(Looper.getMainLooper());


    public interface GeoCallBack {
        void onSuccess(double lat, double lon);
        void onError(GeoLocatorException geoLocatorException);
    }


    public static int[] convertCoordinates(String cityName) {
        int[] latLong = new int[2];
        try {
            String stringURL = String.format(API_URL, cityName.replace(" ", "+"));
            URL url = new URL(stringURL);

            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.err.println("Error while trying to convert the city's name to coordinates");

        }
    }
}
