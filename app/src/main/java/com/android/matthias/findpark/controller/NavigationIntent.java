package com.android.matthias.findpark.controller;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.android.matthias.findpark.R;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Matthias on 14/12/2014.
 */
public class NavigationIntent implements FloatingActionButton.OnClickListener{
    private Context context;
    private LatLng current;
    private LatLng target;

    public NavigationIntent(Context context, LatLng current, LatLng target) {
        this.context = context;
        this.current = current;
        this.target = target;
    }

    @Override
    public void onClick(View v) {
        String params =
                "?saddr=" + current.latitude + "," + current.longitude +
                "&daddr=" + target.latitude + "," + target.longitude;
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse(context.getString(R.string.google_maps_intent) + params));
        this.context.startActivity(intent);
    }
}
