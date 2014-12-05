package com.test.matthias.justparktest.model;

import java.util.List;

/**
 * Created by Matthias on 04/12/2014.
 */
public class Parking {
    private String title;
    private String href;
    private int isApiEnabled;
    private Category category;
    private boolean available;
    private List<Facility> facilities;
    private boolean instantBookings;
    private int spacesToRent;
    private Coords coords;
    private Feedback feedback;
    private Photos photos;
    private Currency currency;
    private Distance distance;
    private DisplayPrice displayPrice;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public int getIsApiEnabled() {
        return isApiEnabled;
    }

    public void setIsApiEnabled(int isApiEnabled) {
        this.isApiEnabled = isApiEnabled;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public List<Facility> getFacilities() {
        return facilities;
    }

    public void setFacilities(List<Facility> facilities) {
        this.facilities = facilities;
    }

    public boolean isInstantBookings() {
        return instantBookings;
    }

    public void setInstantBookings(boolean instantBookings) {
        this.instantBookings = instantBookings;
    }

    public int getSpacesToRent() {
        return spacesToRent;
    }

    public void setSpacesToRent(int spacesToRent) {
        this.spacesToRent = spacesToRent;
    }

    public Coords getCoords() {
        return coords;
    }

    public void setCoords(Coords coords) {
        this.coords = coords;
    }

    public Feedback getFeedback() {
        return feedback;
    }

    public void setFeedback(Feedback feedback) {
        this.feedback = feedback;
    }

    public Photos getPhotos() {
        return photos;
    }

    public void setPhotos(Photos photos) {
        this.photos = photos;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Distance getDistance() {
        return distance;
    }

    public void setDistance(Distance distance) {
        this.distance = distance;
    }

    public DisplayPrice getDisplayPrice() {
        return displayPrice;
    }

    public void setDisplayPrice(DisplayPrice displayPrice) {
        this.displayPrice = displayPrice;
    }
}
