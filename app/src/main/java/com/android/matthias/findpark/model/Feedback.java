package com.android.matthias.findpark.model;

import com.android.matthias.findpark.exception.RatingException;

/**
 * Created by Matthias on 04/12/2014.
 */
public class Feedback {
    private float rating;
    private int count;

    public Feedback(float rating, int count) throws RatingException {
        if (rating < 0 || rating > 5) {
            throw new RatingException();
        }
        this.rating = rating;
        this.count = count;
    }

    public float getRating() {
        return rating;
    }

    public int getCount() {
        return count;
    }
}
