package com.test.matthias.justparktest.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Matthias on 04/12/2014.
 */
public enum Facility {
    @SerializedName("24/7 access")
    FULL_ACCESS ("24/7 access"),
    @SerializedName("CCTV")
    CCTV ("CCTV"),
    @SerializedName("Security guards")
    SECURITY_GUARDS ("Security guards"),
    @SerializedName("Security key")
    SECURITY_KEY ("Security key"),
    @SerializedName("Security gates")
    SECURITY_GATES ("Security gates"),
    @SerializedName("Sheltered parking")
    SHELTERED_PARKING ("Sheltered parking"),
    @SerializedName("Underground parking")
    UNDERGROUND_PARKING ("Underground parking"),
    @SerializedName("WC")
    WC ("WC"),
    @SerializedName("Car wash available")
    CAR_WASH_AVAILABLE ("Car wash available"),
    @SerializedName("Lighting")
    LIGHTING ("Lighting"),
    @SerializedName("Arranged transfers")
    ARRANGED_TRANSFERTS("Arranged transfers");

    private String name = "";

    Facility(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
