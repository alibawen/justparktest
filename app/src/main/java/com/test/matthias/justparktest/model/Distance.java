package com.test.matthias.justparktest.model;

/**
 * Created by Matthias on 04/12/2014.
 */
public class Distance {
    private double mile;
    private double km;

    public Distance(double mile, double km) {
        this.mile = mile;
        this.km = km;
    }

    public double getMile() {
        return mile;
    }

    public double getKm() {
        return km;
    }
}
