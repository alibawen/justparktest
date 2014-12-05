package com.test.matthias.justparktest.model;

/**
 * Created by Matthias on 04/12/2014.
 */
public class Photo {
    private String url;
    private int width;
    private int height;

    public Photo(String url, int width, int height) {
        this.url = url;
        this.width = width;
        this.height = height;
    }

    public String getUrl() {
        return url;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
