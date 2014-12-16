package com.android.matthias.findpark.webservice;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * Created by Matthias on 07/12/2014.
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

    private static final int BITMAP_HEIGHT = 300; // in pixels
    private ImageView imageView;
    private View loadingView;

    public DownloadImageTask(ImageView imageView, View loadingView) {
        this.imageView = imageView;
        this.loadingView = loadingView;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.imageView.setVisibility(View.INVISIBLE);
        this.loadingView.setVisibility(View.VISIBLE);
    }

    @Override
    protected Bitmap doInBackground(String... URL) {

        String imageURL = URL[0];

        Bitmap bitmap = null;
        try {
            // Download Image from URL
            InputStream input = new java.net.URL(imageURL).openStream();
            // Decode Bitmap
            bitmap = BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bitmap != null) {
            return this.getResizedHeight(bitmap, BITMAP_HEIGHT);
        } else {
            return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        if (result != null) {
            // Set the bitmap into ImageView
            this.imageView.setImageBitmap(result);
            this.imageView.setVisibility(View.VISIBLE);
        }
        this.loadingView.setVisibility(View.INVISIBLE);
    }

    public Bitmap getResizedHeight(Bitmap bm, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();

        if (newHeight != 0 || height != 0) {
            float ratio = (float) height / newHeight;
            float newWidth = (float) width / ratio;

            return this.getResizedBitmap(bm, newHeight, (int) newWidth);
        } else {
            return null;
        }
    }
    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();

        if (newWidth != 0 || width != 0) {
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;

            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        } else {
            return null;
        }
    }
}
