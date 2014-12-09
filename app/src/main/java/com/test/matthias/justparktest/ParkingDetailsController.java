package com.test.matthias.justparktest;

import android.app.Activity;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.test.matthias.justparktest.model.Parking;
import com.test.matthias.justparktest.webservice.DownloadImageTask;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Matthias on 07/12/2014.
 */
public class ParkingDetailsController {

    private Activity context;
    private TextView titleView;
    private RatingBar ratingBar;
    private TextView priceView;
    private TextView periodView;
    private ImageView photoView;


    public ParkingDetailsController(Activity context) {
        this.context = context;
        this.titleView = (TextView) context.findViewById(R.id.title);
        this.ratingBar = (RatingBar) context.findViewById(R.id.ratingBar);
        this.priceView = (TextView) context.findViewById(R.id.price);
        this.periodView = (TextView) context.findViewById(R.id.period);
        this.photoView = (ImageView) context.findViewById(R.id.photo);
    }

    public void displayParkingInfos(Parking parking){
        // Set text
        this.titleView.setText(parking.getTitle());
        this.priceView.setText(parking.getDisplayPrice().getFormattedPrice());
        String period = context.getString(R.string.period_separator) + parking.getDisplayPrice().getPeriod();
        this.periodView.setText(period);

        // Rating Bar
        this.ratingBar.setRating(parking.getFeedback().getRating());

        // TODO: Reset photo

        // Download photo
        DownloadImageTask downloadImageTask = new DownloadImageTask(photoView);
        String photoUrl;
        if (parking.getPhotos().getUserAdded() != null &&
            parking.getPhotos().getUserAdded().getNormal() != null ) {
            photoUrl = parking.getPhotos().getUserAdded().getNormal().getUrl();
        } else {
            photoUrl = parking.getPhotos().getGoogleStreetview();
        }
        downloadImageTask.execute(photoUrl);
    }
}
