package com.example.weebther.Database.Remote.RemoteModels;

import com.google.gson.annotations.SerializedName;

public class WeatherAlert {
    @SerializedName("sender_name")
    public String senderName;
    @SerializedName("event")
    public String event;
    @SerializedName("start")
    public long start;
    @SerializedName("end")
    public long end;
    @SerializedName("description")
    public String description;
}
