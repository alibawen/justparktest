package com.test.matthias.justparktest;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.google.android.gms.internal.pa;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.test.matthias.justparktest.model.Parking;
import com.test.matthias.justparktest.model.QueryResponse;
import com.test.matthias.justparktest.webservice.DownloadParkingListener;
import com.test.matthias.justparktest.webservice.DownloadParkingTask;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends ActionBarActivity implements GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private ParkingDetailsController parkingDetailsController;
    private String WEB_SERVICE_URL;
    private Map<Marker, Integer> markerToParkingIndex;
    private QueryResponse response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.WEB_SERVICE_URL = getString(R.string.data_root_url) + getString(R.string.location_endpoint);
        this.markerToParkingIndex = new HashMap<>();
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        this.parkingDetailsController = new ParkingDetailsController(this);
        this.mMap.setOnMarkerClickListener(this);
        downloadParkings();
    }

    /**
     * Download file from webservice
     */
    private void downloadParkings() {
        DownloadParkingListener listener = new DownloadParkingListener(this);
        DownloadParkingTask task = new DownloadParkingTask(listener, this);
        try {
            URL url = new URL(WEB_SERVICE_URL);
            task.execute(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method called when the download is finished
     * @param response the web service response
     */
    public void onDownloadFinished(QueryResponse response) {
        this.response = response;
        // Display the markers and fill the map
        for (int i = 0; i < response.getParkings().size(); i++) {
            Parking parking = response.getParkings().get(i);
            LatLng coords = new LatLng(parking.getCoords().getLat(), parking.getCoords().getLng());
            Marker marker = mMap.addMarker(new MarkerOptions().position(coords).title(parking.getTitle()));
            this.markerToParkingIndex.put(marker, i);
        }

        // Create user marker
        LatLng userCoords = new LatLng(response.getCoords().getLat(), response.getCoords().getLng());
        // Marker marker = mMap.addMarker(new MarkerOptions().position(userCoords));
        // Move the camera to the user point
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userCoords, 15));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        int index = markerToParkingIndex.get(marker);
        Parking parking = response.getParkings().get(index);
        parkingDetailsController.displayParkingInfos(parking);
        return true;
    }
}
