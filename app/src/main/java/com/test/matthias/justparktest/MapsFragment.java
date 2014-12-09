package com.test.matthias.justparktest;

import android.os.Bundle;
import android.app.Fragment;

import com.test.matthias.justparktest.model.QueryResponse;


/**
 * A fragment to retain the WebService data
 */
public class MapsFragment extends Fragment {
    // Web Service response we want to retain
    private QueryResponse response;
    private int selectedMarkerIndex;

    // this method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    public QueryResponse getResponse() {
        return response;
    }

    public void setResponse(QueryResponse response) {
        this.response = response;
    }

    public int getSelectedMarkerIndex() {
        return selectedMarkerIndex;
    }

    public void setSelectedMarkerIndex(int selectedMarkerIndex) {
        this.selectedMarkerIndex = selectedMarkerIndex;
    }
}
