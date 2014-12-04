package connection;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matthias on 28/11/2014.
 */
public final class RequestManager {

    /**
     * Perform a GET request on the given urls
     * @param urls a list of web service urls
     * @return the list of web services responses
     */
    public static List<String> getRequest(URL... urls) {
        int count = urls.length;
        List<String> responses = new ArrayList<String>(count);
        for (URL url : urls) {
            responses.add(getRequest(url));
        }
        return responses;
    }

    /**
     * Perform a GET request on the given url
     * @param url the web service url
     * @return the web service response (plain text)
     */
    public static String getRequest(URL url) {
        InputStream inputStream = null;
        String result = "";
        try {
            // Create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // Make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url.toString()));

            // Receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
}
