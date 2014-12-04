package connection;

/**
 * Created by Matthias on 24/11/2014.
 */
public interface IDownloadListener {

    public void onDownloadStarted();
    public void onProgressUpdated(String... strings);
    public void onDownloadFinished(String result);
}
