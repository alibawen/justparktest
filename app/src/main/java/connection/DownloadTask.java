package connection;

import android.os.AsyncTask;

import java.net.URL;

/**
 * Created by Matthias on 24/11/2014.
 */
public abstract class DownloadTask extends AsyncTask<URL,String,String> {

    private final IDownloadListener listener;

    public DownloadTask(IDownloadListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        listener.onDownloadStarted();
    }

    @Override
    protected void onPostExecute(String result) {
        listener.onDownloadFinished(result);
    }

    @Override
    protected void onProgressUpdate(String... values) {
        listener.onProgressUpdated(values);
    }
}
