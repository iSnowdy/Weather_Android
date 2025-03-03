package com.example.weebther.Utils;

import android.content.Context;

public class WeatherFormatter {
    public enum UnitType {
        TEMPERATURE, WIND_SPEED, RAIN_VOLUME, PERCENTAGE, UV_INDEX
    }

    // To obtain the UnitSystem from SharedPreferences
    public static UnitSystem getPreferredUnitSystem(Context context) {
        String unitPref = SharedPreferencesManager.getUnitPreference(context);
        return unitPref.equals(UnitSystem.METRIC.getValue()) ? UnitSystem.METRIC : UnitSystem.IMPERIAL;
    }

    // Dynamically formats the unit system and also converts it to the preferred one
    public static String formatUnit(Context context, double value, UnitType type) {
        UnitSystem unitSystem = getPreferredUnitSystem(context);

        switch (type) {
            case TEMPERATURE:
                return String.format("%.1f%s", convertTemperature(value, unitSystem), unitSystem == UnitSystem.METRIC ? "°C" : "°F");

            case WIND_SPEED:
                return String.format("%.1f %s", convertWindSpeed(value, unitSystem), unitSystem == UnitSystem.METRIC ? "m/s" : "mph");

            case RAIN_VOLUME:
                return value > 0 ? String.format("%.1f mm", value) : "No rain";

            case PERCENTAGE:
                return String.format("%.1f%%", value * 100);

            case UV_INDEX:
                return String.format("UV Index: %.1f", value);

            default:
                return String.valueOf(value);
        }
    }

    private static double convertTemperature(double value, UnitSystem unitSystem) {
        return unitSystem == UnitSystem.METRIC ? value : (value * 9 / 5) + 32;
    }

    private static double convertWindSpeed(double value, UnitSystem unitSystem) {
        return unitSystem == UnitSystem.METRIC ? value : value * 2.237;
    }
}
