package com.test.matthias.justparktest.model;

import com.test.matthias.justparktest.exception.RatingException;

/**
 * Created by Matthias on 04/12/2014.
 */
public class Feedback {
    private double rating;
    private int count;

    public Feedback(double rating, int count) throws RatingException {
        if (rating < 0 || rating > 5) {
            throw new RatingException();
        }
        this.rating = rating;
        this.count = count;
    }

    public double getRating() {
        return rating;
    }

    public int getCount() {
        return count;
    }
}
