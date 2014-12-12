package com.test.matthias.justparktest;

import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.test.matthias.justparktest.model.Parking;
import com.test.matthias.justparktest.model.QueryResponse;
import com.test.matthias.justparktest.webservice.DownloadParkingListener;
import com.test.matthias.justparktest.webservice.DownloadParkingTask;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import connection.InternetChecker;

public class MapsActivity extends ActionBarActivity implements GoogleMap.OnMarkerClickListener, SearchView.OnQueryTextListener {

    public static final float ANCHOR_POINT_PORTRAIT = 0.33f;
    public static final float ANCHOR_POINT_LANDSCAPE = 1f;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private SearchView searchView;
    private ParkingDetailsController parkingDetailsController;
    private String WEB_SERVICE_URL;
    private Map<Marker, Integer> markerToParkingIndex;
    private MapsFragment mapsFragment;
    private int selectedMarkerIndex = -1;

    private SlidingUpPanelLayout slidingUpPanel;

    // Connection tentatives
    private int retry = 0;
    private final static int MAX_RETRIES = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(InternetChecker.isNetworkAvailable(this)) {
            this.WEB_SERVICE_URL = getString(R.string.data_root_url) + getString(R.string.location_endpoint);
            this.markerToParkingIndex = new HashMap<>();
            setContentView(R.layout.activity_maps);

            // Set searchView
            this.searchView = (SearchView) findViewById(R.id.searchView);
            this.searchView.setOnQueryTextListener(this);
            // Set default value in search query
            this.searchView.setQuery(this.getString(R.string.default_location), false);

            // Set slidingUpPanel config
            this.slidingUpPanel = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                this.slidingUpPanel.setAnchorPoint(ANCHOR_POINT_LANDSCAPE);
            } else {
                this.slidingUpPanel.setAnchorPoint(ANCHOR_POINT_PORTRAIT);
            }

            this.slidingUpPanel.hidePanel();
            this.slidingUpPanel.setSlidingEnabled(false);
            setUpMapIfNeeded();
        } else {
            Toast.makeText(this, getString(R.string.active_connection_required), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(InternetChecker.isNetworkAvailable(this)) {
            setUpMapIfNeeded();
        } else {
            Toast.makeText(this, getString(R.string.active_connection_required), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // store the data in the fragment
        mapsFragment.setSelectedMarkerIndex(this.selectedMarkerIndex);
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
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUp();
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
        this.displayParkingData();
    }

    /**
     * Get data from fragment if it exists
     * or download data from Web Service
     */
    private void displayParkingData() {
        boolean isCreated = createFragmentIfNeeded();
        if (isCreated) {
            // The fragment doesn't exist, we download the data
            downloadParkings(this.getString(R.string.default_location));
        } else {
            // The fragment already exists
            QueryResponse response = mapsFragment.getResponse();
            this.displayMarkers(response);
            this.selectedMarkerIndex = mapsFragment.getSelectedMarkerIndex();
            if (this.selectedMarkerIndex != -1) {
                this.updateSlidingView(selectedMarkerIndex);
            }
        }
    }

    /**
     * Create a fragment to store web response
     * @return true if the fragment is created.
     */
    public boolean createFragmentIfNeeded() {
        // find the retained fragment on activity restarts
        FragmentManager fm = getFragmentManager();
        mapsFragment = (MapsFragment) fm.findFragmentByTag(getString(R.string.fragment_data_id));

        // create the fragment and data the first time
        if (mapsFragment == null) {
            // add the fragment
            mapsFragment = new MapsFragment();
            fm.beginTransaction().add(mapsFragment, getString(R.string.fragment_data_id)).commit();
            return true;
        }
        return false;
    }

    /**
     * Download file from webservice
     */
    private void downloadParkings(String params) {
        // Download
        DownloadParkingListener listener = new DownloadParkingListener(this);
        DownloadParkingTask task = new DownloadParkingTask(listener, this);
        try {
            String encodedParams = URLEncoder.encode(params, "UTF-8");
            URL url = new URL(WEB_SERVICE_URL + getString(R.string.query_params_id) + encodedParams);
            task.execute(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method called when the download is finished
     * @param response the web service response
     */
    public void onDownloadFinished(QueryResponse response) {
        // Check if the download has worked
        if (response != null) {
            // load the data from the web into the fragment
            mapsFragment.setResponse(response);
            displayMarkers(response);
        } else if(this.retry < MAX_RETRIES) {
            downloadParkings(this.searchView.getQuery().toString());
            this.retry++;
        } else {
            Toast.makeText(this, getString(R.string.server_unreachable), Toast.LENGTH_LONG).show();
        }
    }

    private void displayMarkers(QueryResponse response) {
        // Display the markers and fill the map
        for (int i = 0; i < response.getParkings().size(); i++) {
            Parking parking = response.getParkings().get(i);
            LatLng coords = new LatLng(parking.getCoords().getLat(), parking.getCoords().getLng());
            BitmapDescriptor bitmap = getMarkerIcon(parking);
            Marker marker = mMap.addMarker(
                    new MarkerOptions().position(coords).title(parking.getTitle()).icon(bitmap));
            this.markerToParkingIndex.put(marker, i);
        }

        // Create user marker
        LatLng userCoords = new LatLng(response.getCoords().getLat(), response.getCoords().getLng());
        // Marker marker = mMap.addMarker(new MarkerOptions().position(userCoords));
        // Move the camera to the user point
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userCoords, 15));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
    }

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

    @Override
    public boolean onMarkerClick(Marker marker) {
        int index = markerToParkingIndex.get(marker);

        // Save the index in Fragment if the Activity is destroyed
        this.selectedMarkerIndex = index;
        mapsFragment.setSelectedMarkerIndex(this.selectedMarkerIndex);

        updateSlidingView(index);
        return false; // false: Let the default behaviour occur on the map (info, center)
    }

    private void updateSlidingView(int index) {
        // Authorize to slide
        this.slidingUpPanel.setSlidingEnabled(true);
        this.slidingUpPanel.showPanel();

        // Set data on panel
        QueryResponse response = mapsFragment.getResponse();
        Parking parking = response.getParkings().get(index);
        parkingDetailsController.displayParkingInfos(parking);
    }

    /**
     * Check on submit searchView
     * @param query the text submitted
     * @return
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        this.downloadParkings(query);
        // Remove focus (hide keyboard)
        this.searchView.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
