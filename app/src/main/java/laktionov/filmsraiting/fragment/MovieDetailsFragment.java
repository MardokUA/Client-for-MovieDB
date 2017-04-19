package laktionov.filmsraiting.fragment;


import android.content.ContentValues;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;

import laktionov.filmsraiting.BuildConfig;
import laktionov.filmsraiting.R;
import laktionov.filmsraiting.extras.DownloadVideoAsyncTask;
import laktionov.filmsraiting.extras.Film;
import laktionov.filmsraiting.rest.BaseApi;
import laktionov.filmsraiting.rest.model.Movie;
import laktionov.filmsraiting.rest.model.Video;
import laktionov.filmsraiting.provider.FavoritesProvider;
import laktionov.filmsraiting.provider.FilmsContract;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieDetailsFragment extends Fragment implements View.OnClickListener {

    private static final String LOG_TAG = "........";

    private ArrayList<Video> videoList;
    private Film film;
    private YouTubePlayer player;
    private long film_id;
    private boolean inFavorites;
    private DecimalFormat formatter = new DecimalFormat("#,###.00");
    private FloatingActionButton fab;
    private String language;

    private ImageView img_film_poster;
    private TextView tv_film_originalTitle, tv_film_title, tv_film_releaseDate, tv_film_voteAverage,
            tv_film_budget, tv_film_homepage, tv_film_popularity, tv_film_revenue, tv_film_runtime,
            tv_film_status, tv_film_tagLine, tv_film_overview, tv_film_voteCount,
            tv_film_originalLanguage;
    private YouTubePlayerSupportFragment youtubeFragment;
    private RelativeLayout film_youtube_fragment;
    private RatingBar ratingBar;

    public MovieDetailsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        film_id = getArguments().getLong("film_id");
        inFavorites = getArguments().getBoolean("inFavorites");

        if (!inFavorites) {
            Cursor cursor = getContext().getContentResolver().query(FavoritesProvider.CONTENT_URI, new String[]{
                    FilmsContract.Favorite._ID,
                    FilmsContract.Favorite.FILM_ID
            }, null, null, null);
            while (cursor.moveToNext()) {
                try {
                    long id = cursor.getLong(cursor.getColumnIndex(FilmsContract.Favorite.FILM_ID));
                    if (id == film_id) {
                        inFavorites = true;
                        break;
                    }
                } catch (Exception ex) {
                    Log.d(LOG_TAG, "Empty");
                }
            }
            cursor.close();
        }

        Log.d(TAG, "EXECUTING 1");
        try {

            URL film_url = new URL(BaseApi.API_URL_BASE + "/3" + BaseApi.API_URL_MOVIE_DETAILS + film_id + "?api_key=" + BuildConfig.THE_MOVIEDB_API_KEY + "&language=en-US&page=1");
            new DetailsDownloaderAsyncTask().execute(film_url);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "EXECUTING 2");

        try {
            DownloadVideoAsyncTask task = new DownloadVideoAsyncTask();
            task.execute(film_id);
            try {
                videoList = new ArrayList<>();
                videoList = task.get();
                Log.d(TAG, videoList.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_movie_details, container, false);

        fab = (FloatingActionButton) this.getActivity().findViewById(R.id.fab);
        if (fab != null) {
            if (inFavorites) {
                fab.setImageResource(R.drawable.dark_heart);
            } else {
                fab.setImageResource(R.drawable.heart);
            }
            fab.setForegroundGravity(Gravity.START);
            fab.setOnClickListener(this);
        }
        film_youtube_fragment = (RelativeLayout) root.findViewById(R.id.film_youtube_fragment);
        youtubeFragment = YouTubePlayerSupportFragment.newInstance();
        youtubeFragment.initialize(BuildConfig.YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
                if (!wasRestored) {
                    player = youTubePlayer;
                    player.setFullscreen(false);
                    if (videoList.size() != 0 || videoList != null) {
                        player.cueVideo(videoList.get(0).getKey());
                    }
                    player.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                        @Override
                        public void onFullscreen(boolean b) {
                            if (b) {
                                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                            }
                            if (!b) {
                                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_USER);
                            }
                        }
                    });
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        });

        getChildFragmentManager().beginTransaction()
                .add(R.id.film_youtube_fragment, youtubeFragment)
                .commit();

        img_film_poster = (ImageView) root.findViewById(R.id.img_film_posterPath);
        tv_film_originalTitle = (TextView) root.findViewById(R.id.tv_film_originalTitle);
        tv_film_title = (TextView) root.findViewById(R.id.tv_film_title);
        tv_film_releaseDate = (TextView) root.findViewById(R.id.tv_film_releaseDate);
        tv_film_runtime = (TextView) root.findViewById(R.id.tv_film_runtime);
        tv_film_voteAverage = (TextView) root.findViewById(R.id.tv_film_voteAverage);

        ratingBar = (RatingBar) root.findViewById(R.id.rb_film);
        tv_film_budget = (TextView) root.findViewById(R.id.tv_film_budget);
        tv_film_revenue = (TextView) root.findViewById(R.id.tv_film_revenue);
        tv_film_homepage = (TextView) root.findViewById(R.id.tv_film_homepage);
        tv_film_originalLanguage = (TextView) root.findViewById(R.id.tv_film_originalLanguage);
        tv_film_popularity = (TextView) root.findViewById(R.id.tv_film_popularity);
        tv_film_status = (TextView) root.findViewById(R.id.tv_film_status);
        tv_film_tagLine = (TextView) root.findViewById(R.id.tv_film_tagLine);
        tv_film_voteCount = (TextView) root.findViewById(R.id.tv_film_voteCount);
        tv_film_overview = (TextView) root.findViewById(R.id.tv_film_overview);

        return root;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                if (inFavorites) {
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                        Toast.makeText(getContext(), "\"Already in Favorites\"", Toast.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(v, "Already in \"Favorites\" ", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null)
                                .show();
                    }
                } else {
                    ContentValues cv = new ContentValues();
                    Log.d(LOG_TAG, "film_id = " + film.getId());
                    cv.put("film_id", film.getId());
                    cv.put("poster_path", film.getPoster_path());
                    cv.put("title", film.getOriginal_title());
                    cv.put("film", "movie");

                    getContext().getContentResolver()
                            .insert(FavoritesProvider.CONTENT_URI, cv);

                    inFavorites = true;
                    fab.setImageResource(R.drawable.dark_heart);

                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                        Toast.makeText(getContext(), "Added in \"Favorites\"", Toast.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(v, "added to \"Favorites\":  " + tv_film_originalTitle.getText(), Snackbar.LENGTH_SHORT)
                                .setAction("Action", null)
                                .show();
                    }
                }
                break;
        }
    }

    private class DetailsDownloaderAsyncTask extends AsyncTask<URL, Void, Movie> {
        private Gson gson;

        public DetailsDownloaderAsyncTask() {
            gson = new Gson();
        }

        @Override
        protected Movie doInBackground(URL... urls) {
            try {
                return gson.fromJson(IOUtils.toString(urls[0].openStream()), Movie.class);
            } catch (JsonSyntaxException | IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Movie movie) {
            film = movie;

            if (movie.getPoster_path() == null) {
                img_film_poster.setImageResource(R.drawable.no_image_avavailable);
            } else {
                Picasso.with(getContext()).load(BaseApi.API_URL_IMAGE + movie.getPoster_path())
                        .resize(350, 500)
                        .into(img_film_poster);
            }
            language = movie.getOriginal_language();

            tv_film_originalTitle.setText(movie.getOriginal_title());
            tv_film_title.setText(movie.getTitle());
            tv_film_releaseDate.setText("Release date: " + movie.getRelease_date());
            tv_film_runtime.setText(movie.getRuntime() == null ? "   -" : "Run time: " + movie.getRuntime().toString() + "min");
            tv_film_voteAverage.setText("Vote average: " + movie.getVote_average().toString());
            tv_film_budget.setText(movie.getBudget() == 0 ? "   -" : formatter.format(movie.getBudget()) + "$");
            tv_film_revenue.setText(movie.getRevenue() == 0 ? "   -" : formatter.format(movie.getRevenue()) + "$");
            tv_film_homepage.setText(movie.getHomepage() == null || movie.getHomepage().equals("") ? "   -" : movie.getHomepage());
            tv_film_popularity.setText(movie.getPopularity().toString());
            tv_film_status.setText(movie.getStatus());
            tv_film_tagLine.setText(movie.getTagline() == null || movie.getTagline().equals("") ? "   -" : movie.getTagline());
            tv_film_overview.setText(movie.getOverview());
            tv_film_voteCount.setText(movie.getVote_count().toString());
            tv_film_originalLanguage.setText(setLanguage(language));
            ratingBar.setRating((Float.parseFloat(String.valueOf(movie.getVote_average()))) / 2);
        }
    }

    private String setLanguage(String lang) {
        lang = "UNKNOWN LANGUAGE";
        switch (lang) {
            case "en":
                lang = "English";
                break;
            case "ru":
                lang = "Russian";
                break;
            case "fr":
                lang = "France";
                break;
            case "de":
                lang = "Germany";
                break;
            case "it":
                lang = "Italy";
                break;
            case "sp":
                lang = "Spain";
                break;
        }
        return lang;
    }
}
