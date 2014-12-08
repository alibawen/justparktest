package com.test.matthias.justparktest;

import android.app.Activity;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.matthias.justparktest.model.Parking;
import com.test.matthias.justparktest.webservice.DownloadImageTask;

/**
 * Created by Matthias on 07/12/2014.
 */
public class ParkingDetailsController {

    Activity context;
    private TextView titleView;
    private TextView priceView;
    private ImageView photoView;


    public ParkingDetailsController(Activity context) {
        this.context = context;
        this.titleView = (TextView) context.findViewById(R.id.title);
        this.priceView = (TextView) context.findViewById(R.id.price);
        this.photoView = (ImageView) context.findViewById(R.id.photo);
    }

    public void displayParkingInfos(Parking parking){
        // Set text
        this.titleView.setText(parking.getTitle());
        this.priceView.setText(parking.getDisplayPrice().getFormattedPrice());

        // TODO: Reset photo

        // Download photo
        DownloadImageTask downloadImageTask = new DownloadImageTask(photoView);
        downloadImageTask.execute(parking.getPhotos().getGoogleStreetview());
    }
}
