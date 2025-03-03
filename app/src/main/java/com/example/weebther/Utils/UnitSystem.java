package com.example.weebther.Utils;

public enum UnitSystem {
    METRIC("metric"),
    IMPERIAL("imperial");

    private final String value;

    UnitSystem(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
