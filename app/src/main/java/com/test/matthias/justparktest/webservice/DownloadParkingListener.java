package com.test.matthias.justparktest.webservice;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.test.matthias.justparktest.controller.MapsActivity;
import com.test.matthias.justparktest.R;
import com.test.matthias.justparktest.model.QueryResponse;

import connection.IDownloadListener;

/**
 * Created by Matthias on 29/11/2014.
 */
public class DownloadParkingListener implements IDownloadListener {

    private MapsActivity context;
    private ProgressDialog progressDialogDownload;

    public DownloadParkingListener(MapsActivity context) {
        this.context = context;
    }

    /**
     * Display the progressDialog
     */
    @Override
    public void onDownloadStarted() {
        // Lock the screen orientation to avoid activity to be recreated
        lockScreenOrientation();
        // Create download progress dialog
        progressDialogDownload = new ProgressDialog(context);
        progressDialogDownload.setMessage(context.getString(R.string.downloading_resource));
        progressDialogDownload.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialogDownload.setCancelable(false);
        progressDialogDownload.show();
    }

    /**
     * Update the progressDialog
     * @param progress ?% completion
     */
    @Override
    public void onProgressUpdated(String... progress) {
        progressDialogDownload.setProgress(Integer.parseInt(progress[0]));
    }

    /**
     * Close the progressDialog and extract the zip file
     * @param result
     */
    @Override
    public void onDownloadFinished(String result) {

        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        QueryResponse response = gson.fromJson(result, QueryResponse.class);
        // Close download progress dialog
        if (progressDialogDownload != null) {
            progressDialogDownload.dismiss();
        }

        // Set back screen orientation with sensor
        unlockScreenOrientation();

        // Display markers
        context.onDownloadFinished(response);
    }

    /**
     * Lock the orientation of the screen with the current configuration
     */
    private void lockScreenOrientation() {
        int currentOrientation = context.getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    /**
     * Unlock the orientation of the screen using the sensor
     */
    private void unlockScreenOrientation() {
        context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

}
