package com.android.matthias.findpark.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Matthias on 04/12/2014.
 */
public enum Category {
    @SerializedName("Single level parking lot")
    SINGLE_LEVEL ("Single level parking lot"),
    @SerializedName("Multi level parking lot")
    MULTI_LEVEL ("Multi level parking lot"),
    @SerializedName("Private garage")
    PRIVATE_GARAGE ("Private garage"),
    @SerializedName("Private driveway")
    PRIVATE_DRIVEWAY ("Private driveway");

    private String name = "";

    Category(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
