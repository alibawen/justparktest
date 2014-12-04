package com.test.matthias.justparktest.webservice;

import android.content.Context;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import connection.DownloadTask;
import connection.IDownloadListener;

/**
 * Created by Matthias on 29/11/2014.
 */
public class DownloadParkingTask extends DownloadTask {

    private Context context;

    public DownloadParkingTask(IDownloadListener listener, Context context) {
        super(listener);
        this.context = context;
    }

    @Override
    protected String doInBackground(URL... urls) {
        // Checks if the array is empty
        if (urls.length == 0) {
            return "Error";
        }

        InputStream inputStream;
        String result = "";
        try {
            URL url = urls[0];
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url.toString()));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null) {
                result = convertInputStreamToString(inputStream);
            } else {
                result = "Did not work!";
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line;
        String result = "";
        while((line = bufferedReader.readLine()) != null) {
            result += line;
        }

        inputStream.close();
        return result;

    }

}
