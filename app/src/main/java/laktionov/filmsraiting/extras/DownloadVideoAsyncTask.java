package laktionov.filmsraiting.extras;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.apache.commons.io.IOUtils;

import java.net.URL;
import java.util.ArrayList;

import laktionov.filmsraiting.BuildConfig;
import laktionov.filmsraiting.rest.model.Video;


public class DownloadVideoAsyncTask extends AsyncTask<Long, Video, ArrayList<Video>> {

    private static final String API_URL_BASE = "https://api.themoviedb.org/3/movie/";
    private static final String TAG = "LOG";

    private ArrayList<Video> videoList;
    private Gson gson;

    public DownloadVideoAsyncTask() {
        this.gson = new Gson();
        this.videoList = new ArrayList<>();
    }

    @Override
    protected ArrayList<Video> doInBackground(Long... params) {
        Log.d(TAG, "DOWNLOAD VIDEOS");
        try {
            URL url = new URL(API_URL_BASE + params[0].toString() + "/videos?api_key=" + BuildConfig.THE_MOVIEDB_API_KEY + "&language=en-US");
            Log.d(TAG, "URL " + url);
            JsonObject jsonObject = gson.fromJson(IOUtils.toString(url.openStream()), JsonObject.class);

            JsonArray results = jsonObject.getAsJsonArray("results");

            for (JsonElement element : results) {
                Video video = gson.fromJson(element.toString(), Video.class);
                videoList.add(video);
                Log.d(TAG,"VIDEO ADDED");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return videoList;
    }

}