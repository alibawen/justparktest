package com.test.matthias.justparktest.model;

/**
 * Created by Matthias on 04/12/2014.
 */
public class DisplayPrice {
    private double price;
    private String period;
    private String formattedPrice;

    public DisplayPrice(double price, String period, String formattedPrice) {
        this.price = price;
        this.period = period;
        this.formattedPrice = formattedPrice;
    }

    public double getPrice() {
        return price;
    }

    public String getPeriod() {
        return period;
    }

    public String getFormattedPrice() {
        return formattedPrice;
    }
}
