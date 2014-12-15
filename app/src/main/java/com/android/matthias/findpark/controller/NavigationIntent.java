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
        // Create URL
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(this.context.getString(R.string.intent_scheme))
               .authority(this.context.getString(R.string.intent_authority))
               .appendPath(this.context.getString(R.string.maps))
               .appendQueryParameter(this.context.getString(R.string.saddr),
                       this.current.latitude + "," + this.current.longitude)
               .appendQueryParameter(this.context.getString(R.string.daddr),
                       this.target.latitude + "," + this.target.longitude);

        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse(builder.build().toString()));
        this.context.startActivity(intent);
    }
}
