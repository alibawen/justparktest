package com.android.matthias.findpark.model;

import android.widget.ImageView;

/**
 * Created by Matthias on 11/12/2014.
 */
public class FacilityResource {
    private int imageViewId;
    private int drawableId;
    private ImageView imageView;

    public FacilityResource(int imageViewId, int drawableId) {
        this.imageViewId = imageViewId;
        this.drawableId = drawableId;
    }

    public int getImageViewId() {
        return imageViewId;
    }

    public void setImageViewId(int imageViewId) {
        this.imageViewId = imageViewId;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public void setDrawableId(int drawableId) {
        this.drawableId = drawableId;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }
}
