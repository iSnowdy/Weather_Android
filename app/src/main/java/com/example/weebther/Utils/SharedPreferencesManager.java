package com.example.weebther.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {
    private static final String PREF_NAME = "weather_prefs";
    private static final String KEY_UNIT = "unit_system";
    private static final String DEFAULT_UNIT = "metric";

    public static void saveUnitPreference(Context context, UnitSystem unit) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(KEY_UNIT, unit.getValue()).apply();
    }

    public static String getUnitPreference(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_UNIT, DEFAULT_UNIT);
    }
}
