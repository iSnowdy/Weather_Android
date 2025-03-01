package com.example.weebther.Database.Local.Entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

// Must be Serializable so we can pass objects inside a Bundle

@Entity(tableName = "cities")
public class City implements Serializable {
    @PrimaryKey
    @NonNull
    private String name;
    private String country;
    // Latitude and longitude to not repeat calls to the API
    private double latitude;
    private double longitude;
    private boolean isFavourite; // Favourites menu
    private long lastUpdated; // To keep track of last searches by order

    // Rooms needs this annotation to know which constructor to use
    @Ignore
    public City(@NonNull String name, String country) {
        this.name = name;
        this.country = country;
    }

    public City(@NonNull String name, String country, double latitude, double longitude, boolean isFavourite, long lastUpdated) {
        this.name = name;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    @Override
    public String toString() {
        return "\n-------------City Information-------------" +
                "\nCity Name: " + name +
                "\nCity Country: " + country +
                "\n-----------------------------------------";
    }


    // Getters and Setters
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(long longitude) {
        this.longitude = longitude;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
