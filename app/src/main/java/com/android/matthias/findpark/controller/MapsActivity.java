package com.android.matthias.findpark.controller;

import android.app.FragmentManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.matthias.findpark.R;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.android.matthias.findpark.display.DeviceDisplay;
import com.android.matthias.findpark.model.Parking;
import com.android.matthias.findpark.model.QueryResponse;
import com.android.matthias.findpark.webservice.DownloadParkingListener;
import com.android.matthias.findpark.webservice.DownloadParkingTask;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import connection.InternetChecker;

/**
 * Main Activity
 *
 * Display the Google Maps
 * Download data
 * Store data into Fragment
 * Display parking spaces
 */
public class MapsActivity extends ActionBarActivity implements GoogleMap.OnMarkerClickListener, SearchView.OnQueryTextListener {

    public static float ANCHOR_POINT_PORTRAIT_PX; // in px, stops before showing pictures panel
    public static final float ANCHOR_POINT_LANDSCAPE = 1f; // screen ratio
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private SearchView searchView;
    private ParkingDetailsController parkingDetailsController;
    private Uri.Builder wsBuilder;
    private Map<Marker, Integer> markerToParkingIndex;
    private MapsFragment mapsFragment;
    private int selectedMarkerIndex = -1;

    private SlidingUpPanelLayout slidingUpPanel;

    // Connection attempts
    private int retry = 0;
    private final static int MAX_RETRIES = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (InternetChecker.isNetworkAvailable(this)) {
            // Create web service URL
            this.wsBuilder = new Uri.Builder();
            this.wsBuilder.scheme(this.getString(R.string.scheme))
                    .authority(this.getString(R.string.authority))
                    .appendPath(this.getString(R.string.api_version))
                    .appendPath(this.getString(R.string.location));
            this.markerToParkingIndex = new HashMap<>();
            this.setContentView(R.layout.activity_maps);

            // Set searchView
            this.searchView = (SearchView) this.findViewById(R.id.searchView);
            this.searchView.setOnQueryTextListener(this);
            // Set default value in search query
            this.searchView.setQuery(this.getString(R.string.default_location), false);

            // Get anchor point from views
            LinearLayout shownLayout = (LinearLayout) this.findViewById(R.id.anchor_layout);
            shownLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            ANCHOR_POINT_PORTRAIT_PX = shownLayout.getMeasuredHeight();

            // Set slidingUpPanel config
            this.slidingUpPanel = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                this.slidingUpPanel.setAnchorPoint(ANCHOR_POINT_LANDSCAPE);
            } else {
                float anchorPoint = DeviceDisplay.heightPixelsToRatio(this, ANCHOR_POINT_PORTRAIT_PX);
                this.slidingUpPanel.setAnchorPoint(anchorPoint);
            }

            this.slidingUpPanel.hidePanel();
            this.slidingUpPanel.setSlidingEnabled(false);

            // Set map
            this.setUpMapIfNeeded();
        } else {
            Toast.makeText(this, getString(R.string.active_connection_required), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (InternetChecker.isNetworkAvailable(this)) {
            this.setUpMapIfNeeded();
        } else {
            Toast.makeText(this, getString(R.string.active_connection_required), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // store the data in the fragment
        this.mapsFragment.setSelectedMarkerIndex(this.selectedMarkerIndex);
    }

    /**
     * Close the sliding panel if expanded or anchored
     *
     */
    @Override
    public void onBackPressed() {
        if (this.slidingUpPanel != null &&
                (this.slidingUpPanel.isPanelExpanded() || this.slidingUpPanel.isPanelAnchored())) {
            this.slidingUpPanel.collapsePanel();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUp()} once when {@link #mMap} is not null.
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
        if (this.mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            this.mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (this.mMap != null) {
                this.setUp();
            }
        }
    }

    /**
     * We set up the map along with the controller and download data
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUp() {
        this.parkingDetailsController = new ParkingDetailsController(this);
        this.mMap.setOnMarkerClickListener(this);
        // Prevent compass to overlap the search view
        this.searchView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        this.resetGoogleMapsUIPadding();
        this.displayParkingData();
    }

    /**
     * Reset the Google Maps GUI padding, prevent compass to overlap search bar
     * Use this.searchView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
     * before calling this function
     *
     */
    private void resetGoogleMapsUIPadding() {
        int paddingTop = this.searchView.getMeasuredHeight() + 10;
        this.mMap.setPadding(0, paddingTop, 0, 0);
    }

    /**
     * Get data from fragment if it exists
     * or download data from Web Service
     */
    private void displayParkingData() {
        boolean isCreated = createFragmentIfNeeded();
        if (isCreated) {
            // The fragment doesn't exist, we download the data
            this.downloadParkingSpaces(this.getString(R.string.default_location));
        } else {
            // The fragment already exists
            QueryResponse response = this.mapsFragment.getResponse();
            this.displayMarkers(response);
            this.selectedMarkerIndex = this.mapsFragment.getSelectedMarkerIndex();
            if (this.selectedMarkerIndex != -1) {
                this.updateNavigationButton(this.selectedMarkerIndex);
                this.updateSlidingView(this.selectedMarkerIndex);
            }
        }
    }

    /**
     * Create a fragment to store web response
     *
     * @return true if the fragment is created.
     */
    public boolean createFragmentIfNeeded() {
        // find the retained fragment on activity restarts
        FragmentManager fm = getFragmentManager();
        this.mapsFragment = (MapsFragment) fm.findFragmentByTag(getString(R.string.fragment_data_id));

        // create the fragment and data the first time
        if (this.mapsFragment == null) {
            // add the fragment
            this.mapsFragment = new MapsFragment();
            fm.beginTransaction().add(this.mapsFragment, getString(R.string.fragment_data_id)).commit();
            return true;
        }
        return false;
    }

    /**
     * Download file from webservice asynchronously
     * Calls onDownloadFinished when finished
     */
    private void downloadParkingSpaces(String params) {
        // Download
        DownloadParkingListener listener = new DownloadParkingListener(this);
        DownloadParkingTask task = new DownloadParkingTask(listener, this);
        try {
            String encodedParams = URLEncoder.encode(params, "UTF-8");
            this.wsBuilder.appendQueryParameter(this.getString(R.string.param_q), encodedParams);
            URL url = new URL(this.wsBuilder.build().toString());
            task.execute(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method called when the download is finished
     *
     * @param response of the web service
     */
    public void onDownloadFinished(QueryResponse response) {
        // Check if the download has worked
        if (response != null) {
            // load the data from the web into the fragment
            this.mapsFragment.setResponse(response);
            displayMarkers(response);
        } else if (this.retry < MAX_RETRIES) {
            downloadParkingSpaces(this.searchView.getQuery().toString());
            this.retry++;
        } else {
            Toast.makeText(this, getString(R.string.server_unreachable), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Display the markers on map according to the web service response
     *
     * @param response of the web service
     */
    private void displayMarkers(QueryResponse response) {
        // Display the markers and fill the map
        for (int i = 0; i < response.getParkings().size(); i++) {
            Parking parking = response.getParkings().get(i);
            LatLng coords = new LatLng(parking.getCoords().getLat(), parking.getCoords().getLng());
            BitmapDescriptor bitmap = getMarkerIcon(parking);
            Marker marker = this.mMap.addMarker(
                    new MarkerOptions().position(coords).title(parking.getTitle()).icon(bitmap));
            this.markerToParkingIndex.put(marker, i);
        }

        // Create user marker
        LatLng userCoords = new LatLng(response.getCoords().getLat(), response.getCoords().getLng());
        this.mMap.addMarker(new MarkerOptions().position(userCoords)
                .title(getString(R.string.marker_title_you))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_marker)));
        
        // Move the camera to the user point
        this.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userCoords, 15));
        this.mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
    }

    /**
     * Return a colored marker icon depending of the availability and the instant booking
     * Green : isAvailable() && isInstantBookings()
     * Orange : isAvailable() && !isInstantBookings()
     * Red : !isAvailable() && !isInstantBookings()
     *
     * @param parking
     * @return the BitmapDescriptor icon
     */
    private BitmapDescriptor getMarkerIcon(Parking parking) {
        // Parking and bookings available
        if (parking.isAvailable() && parking.isInstantBookings()) {
            return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
        } else if (parking.isAvailable()) {
            // Parking available only
            return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
        } else {
            // Nothing available
            return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
        }
    }

    /**
     * Marker onClick event
     * Save the index of the current marker in the Fragment
     * Update the sliding view with the details
     *
     * @param marker
     * @return
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        // Check if the a parking is clicked
        if (!marker.getTitle().equals(getString(R.string.marker_title_you))) {
            int index = this.markerToParkingIndex.get(marker);

            // Save the index in Fragment if the Activity is destroyed
            this.selectedMarkerIndex = index;
            this.mapsFragment.setSelectedMarkerIndex(this.selectedMarkerIndex);

            this.updateNavigationButton(index);
            this.updateSlidingView(index);
            return false; // false: Let the default behaviour occur on the map (info, center)
        } else {
            return true; // Do nothing
        }
    }

    /**
     * Display the marker info on the sliding panel
     *
     * @param index of the marker
     */
    private void updateSlidingView(int index) {
        // Authorize to slide and show panel
        this.slidingUpPanel.setSlidingEnabled(true);
        this.slidingUpPanel.showPanel();

        // Set data on panel
        QueryResponse response = this.mapsFragment.getResponse();
        Parking parking = response.getParkings().get(index);
        this.parkingDetailsController.displayParkingInfos(parking);
    }

    private void updateNavigationButton(int index) {
        // Set the icon visible
        FloatingActionButton fab = (FloatingActionButton) this.findViewById(R.id.navigation_button);
        fab.setVisibility(View.VISIBLE);

        // Push Google map GUI
        int paddingTop = this.searchView.getMeasuredHeight() + 10;
        this.mMap.setPadding(0, paddingTop, 0, 110);

        QueryResponse response = this.mapsFragment.getResponse();
        Parking parking = response.getParkings().get(index);

        // Create navigation intent on click
        LatLng current = new LatLng(response.getCoords().getLat(), response.getCoords().getLng());
        LatLng target = new LatLng(parking.getCoords().getLat(), parking.getCoords().getLng());
        NavigationIntent navigationIntent = new NavigationIntent(this, current, target);
        fab.setOnClickListener(navigationIntent);
    }

    /**
     * Check on submit searchView
     *
     * @param query the text submitted
     * @return
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        // Remove focus (hide keyboard)
        this.searchView.clearFocus();
        // Clear the google maps
        this.resetMap();
        // Download new data from query
        this.downloadParkingSpaces(query);
        return true;
    }

    /**
     * Reset the Google Maps and data
     */
    private void resetMap() {
        this.mMap.clear();
        this.selectedMarkerIndex = -1;
        this.markerToParkingIndex.clear();
        this.resetSlidingPanelInfo();
        this.resetGoogleMapsUIPadding();

        // Hide Navigation button
        FloatingActionButton fab = (FloatingActionButton) this.findViewById(R.id.navigation_button);
        fab.setVisibility(View.INVISIBLE);
    }

    /**
     * Reset the sliding panel content
     */
    private void resetSlidingPanelInfo() {
        this.slidingUpPanel.setSlidingEnabled(false);
        TextView title = (TextView) this.findViewById(R.id.title);
        title.setText(R.string.description_dialog);
        RatingBar ratingBar = (RatingBar) this.findViewById(R.id.rating_bar);
        ratingBar.setRating(0);
        TextView ratingCount = (TextView) this.findViewById(R.id.rating_count);
        ratingCount.setText(R.string.rating_text);
        TextView price = (TextView) this.findViewById(R.id.price);
        price.setText("");
        TextView period = (TextView) this.findViewById(R.id.period);
        period.setText("");
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
