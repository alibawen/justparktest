package com.test.matthias.justparktest.controller;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.test.matthias.justparktest.R;
import com.test.matthias.justparktest.model.Facility;
import com.test.matthias.justparktest.model.FacilityResource;
import com.test.matthias.justparktest.model.Parking;
import com.test.matthias.justparktest.webservice.DownloadImageTask;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Matthias on 07/12/2014.
 */
public class ParkingDetailsController {

    private Activity context;
    private TextView titleView;
    private RatingBar ratingBar;
    private TextView ratingCount;
    private TextView category;
    private TextView priceView;
    private TextView periodView;
    private ImageView photoView;

    // Facilities
    private static final Map<Facility, FacilityResource> facilityMap;
    static
    {
        facilityMap = new HashMap<>();
        facilityMap.put(Facility.FULL_ACCESS, new FacilityResource(R.id.full_access, R.drawable.full_access));
        facilityMap.put(Facility.LIGHTING, new FacilityResource(R.id.lighting, R.drawable.lighting));
        facilityMap.put(Facility.SHELTERED_PARKING, new FacilityResource(R.id.shelter, R.drawable.shelter));
        facilityMap.put(Facility.SECURITY_GATES, new FacilityResource(R.id.gate, R.drawable.gate));
        facilityMap.put(Facility.SECURITY_GUARDS, new FacilityResource(R.id.guard, R.drawable.guard));
        facilityMap.put(Facility.CCTV, new FacilityResource(R.id.cctv, R.drawable.cctv));
        facilityMap.put(Facility.SECURITY_KEY, new FacilityResource(R.id.key, R.drawable.key));
        facilityMap.put(Facility.WC, new FacilityResource(R.id.wc, R.drawable.wc));
        facilityMap.put(Facility.CAR_WASH_AVAILABLE, new FacilityResource(R.id.car_wash, R.drawable.car_wash));
        facilityMap.put(Facility.UNDERGROUND_PARKING, new FacilityResource(R.id.underground, R.drawable.underground));
        facilityMap.put(Facility.ARRANGED_TRANSFERTS, new FacilityResource(R.id.arranged_transfer, R.drawable.transfer));
    }

    public ParkingDetailsController(Activity context) {
        this.context = context;
        this.titleView = (TextView) context.findViewById(R.id.title);
        this.ratingBar = (RatingBar) context.findViewById(R.id.rating_bar);
        this.ratingCount = (TextView) context.findViewById(R.id.rating_count);
        this.category = (TextView) context.findViewById(R.id.category_label);
        this.priceView = (TextView) context.findViewById(R.id.price);
        this.periodView = (TextView) context.findViewById(R.id.period);
        this.photoView = (ImageView) context.findViewById(R.id.photo);

        // Facilities
        resetFacilities();
    }

    /**
     * Set transparency for all facility icons
     */
    private void resetFacilities() {
        for(Map.Entry<Facility, FacilityResource> entry : facilityMap.entrySet()) {
            FacilityResource resource = entry.getValue();
            // Create the imageView from resource
            ImageView imageView = (ImageView) context.findViewById(resource.getImageViewId());
            // Set transparency to all facility icons
            this.setTransparency(imageView, resource.getDrawableId());
            // Add drawable in the ImageView
            imageView.setImageDrawable(context.getResources().getDrawable(resource.getDrawableId()));
            // Add ImageView to the FacilityResource
            resource.setImageView(imageView);
        }
    }

    /**
     * Set half opacity
     * @param imageView the imageView which will contain the drawable
     * @param resource the id of the drawable
     */
    private void setTransparency(ImageView imageView, int resource) {
        Drawable drawable = context.getResources().getDrawable(resource);
        // Disable
        drawable.setAlpha(64);
        imageView.setImageDrawable(drawable);
    }

    /**
     * Display parking data on the view
     * @param parking
     */
    public void displayParkingInfos(Parking parking) {
        // Set text
        this.titleView.setText(parking.getTitle());
        this.priceView.setText(parking.getDisplayPrice().getFormattedPrice());
        String period = context.getString(R.string.period_separator) + parking.getDisplayPrice().getPeriod();
        this.periodView.setText(period);

        // Rating Bar
        this.ratingBar.setRating(parking.getFeedback().getRating());
        // Rating count
        this.ratingCount.setText("(" + parking.getFeedback().getCount() + ")");

        // Category
        if(parking.getCategory() != null) {
            this.category.setText(parking.getCategory().toString());
        }
        // TODO: Reset photo

        // Download photo
        downloadPhoto(parking);

        // Set default image (remove transparency) on active facilities.
        resetFacilities();
        for(Facility facility : parking.getFacilities()) {
            FacilityResource resource = facilityMap.get(facility);
            if (resource != null) {
                Drawable drawable = context.getResources().getDrawable(resource.getDrawableId());
                // Enable
                drawable.setAlpha(255);
                resource.getImageView().setImageDrawable(drawable);
            }
        }
    }

    /**
     * Download photo in a AsyncTask
     * @param parking the parking
     */
    private void downloadPhoto(Parking parking) {
        DownloadImageTask downloadImageTask = new DownloadImageTask(photoView);
        String photoUrl;
        if (parking.getPhotos().getUserAdded() != null &&
            parking.getPhotos().getUserAdded().getNormal() != null ) {
            photoUrl = context.getString(R.string.data_root_url)
                    + parking.getPhotos().getUserAdded().getNormal().getUrl();
        } else {
            photoUrl = parking.getPhotos().getGoogleStreetview();
        }
        downloadImageTask.execute(photoUrl);
    }
}
