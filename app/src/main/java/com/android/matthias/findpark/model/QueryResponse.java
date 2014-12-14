package com.android.matthias.findpark.model;

import java.util.List;

/**
 * Created by Matthias on 04/12/2014.
 */
public class QueryResponse {
    private Coords coords;
    private List<Parking> data;

    public Coords getCoords() {
        return coords;
    }

    public List<Parking> getParkings() {
        return data;
    }
}
