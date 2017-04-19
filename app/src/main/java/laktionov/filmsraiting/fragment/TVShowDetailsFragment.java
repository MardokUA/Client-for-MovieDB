package laktionov.filmsraiting.fragment;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import laktionov.filmsraiting.R;
import laktionov.filmsraiting.extras.Film;
import laktionov.filmsraiting.rest.BaseApi;
import laktionov.filmsraiting.rest.model.Movie;
import laktionov.filmsraiting.provider.FavoritesProvider;
import laktionov.filmsraiting.provider.FilmsContract;

public class TVShowDetailsFragment extends Fragment implements View.OnClickListener {

    private Film film;
    private FloatingActionButton fab;
    private long tvshow_id;
    private boolean inFavorites;

    private static final String LOG_TAG = "........";
    private RatingBar ratingBar;
    private String language;
    private ImageView img_tvshow_poster;
    private TextView tv_tvshow_originalName, tv_tvshow_name, tv_tvshow_firstAirDate, tv_tvshow_originalCountry,
            tv_tvshow_voteAverage, tv_tvshow_seasons, tv_tvshow_episodes, tv_tvshow_episodesDates, tv_tvshow_homepage,
            tv_tvshow_originalLanguage, tv_tvshow_popularity, tv_tvshow_status, tv_tvshow_voteCount,
            tv_tvshow_overview;

    public TVShowDetailsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tvshow_id = getArguments().getLong("tvshow_id");
        inFavorites = getArguments().getBoolean("inFavorites");

        if (!inFavorites) {
            Cursor cursor = getContext().getContentResolver().query(FavoritesProvider.CONTENT_URI, null, null, null, null);
            while (cursor.moveToNext()) {
                try {
                    long id = cursor.getLong(cursor.getColumnIndex(FilmsContract.Favorite.FILM_ID));
                    if (id == tvshow_id) {
                        inFavorites = true;
                        break;
                    }
                } catch (Exception ex) {
                    Log.d(LOG_TAG, "Empty");
                }
            }
            cursor.close();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "OPEN TVSHOW DETAILS FRAGMENT");

        View root = inflater.inflate(R.layout.fragment_tvshow_details, container, false);

        fab = (FloatingActionButton) this.getActivity().findViewById(R.id.fab);
        if (fab != null) {
            if (inFavorites) {
                fab.setImageResource(R.drawable.dark_heart);
            } else {
                fab.setImageResource(R.drawable.heart);
            }
            fab.setOnClickListener(this);
        }

        ratingBar = (RatingBar) root.findViewById(R.id.rb_tvshow);
        img_tvshow_poster = (ImageView) root.findViewById(R.id.img_tvshow_posterPath);
        tv_tvshow_originalName = (TextView) root.findViewById(R.id.tv_tvshow_originalName);
        tv_tvshow_name = (TextView) root.findViewById(R.id.tv_tvshow_name);
        tv_tvshow_firstAirDate = (TextView) root.findViewById(R.id.tv_tvshow_firstAirDate);
        tv_tvshow_originalCountry = (TextView) root.findViewById(R.id.tv_tvshow_originalCountry);
        tv_tvshow_voteAverage = (TextView) root.findViewById(R.id.tv_tvshow_voteAverage);

        tv_tvshow_seasons = (TextView) root.findViewById(R.id.tv_tvshow_seasons);
        tv_tvshow_episodes = (TextView) root.findViewById(R.id.tv_tvshow_episodes);
        tv_tvshow_episodesDates = (TextView) root.findViewById(R.id.tv_tvshow_episodesDates);
        tv_tvshow_homepage = (TextView) root.findViewById(R.id.tv_tvshow_homepage);
        tv_tvshow_originalLanguage = (TextView) root.findViewById(R.id.tv_tvshow_originalLanguage);
        tv_tvshow_popularity = (TextView) root.findViewById(R.id.tv_tvshow_popularity);
        tv_tvshow_status = (TextView) root.findViewById(R.id.tv_tvshow_status);
        tv_tvshow_voteCount = (TextView) root.findViewById(R.id.tv_tvshow_voteCount);
        tv_tvshow_overview = (TextView) root.findViewById(R.id.tv_tvshow_overview);
        return root;
    }

    @Override
    public void onStart() {

        try {
            URL tvshow_url = new URL(BaseApi.API_URL_BASE + "/3" + BaseApi.API_URL_TVSHOW_DETAILS + tvshow_id + "?api_key=" + laktionov.filmsraiting.BuildConfig.THE_MOVIEDB_API_KEY + "&language=en-US&page=1");
            new DetailsDownloaderAsyncTask().execute(tvshow_url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        super.onStart();
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
                    cv.put("film_id", film.getId());
                    cv.put("poster_path", film.getPoster_path());
                    cv.put("film", "tvshow");
                    cv.put("title", film.getOriginal_name());

                    getContext().getContentResolver()
                            .insert(FavoritesProvider.CONTENT_URI, cv);

                    inFavorites = true;
                    fab.setImageResource(R.drawable.dark_heart);

                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                        Toast.makeText(getContext(), "Added in \"Favorites\"", Toast.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(v, "added to \"Favorites\":  " + tv_tvshow_originalName.getText(), Snackbar.LENGTH_SHORT)
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
        protected void onPostExecute(Movie tvShow) {

            film = tvShow;

            if (tvShow.getPoster_path() == null) {

                img_tvshow_poster.setImageResource(R.drawable.no_image_avavailable);

            } else {

                Picasso.with(getContext()).load(BaseApi.API_URL_IMAGE + tvShow.getPoster_path())
                        .resize(350, 500)
                        .into(img_tvshow_poster);

            }

            language = tvShow.getOriginal_language();

            tv_tvshow_originalName.setText(tvShow.getOriginal_name());
            ratingBar.setRating(Float.parseFloat(String.valueOf(tvShow.getVote_average())) / 2);
            tv_tvshow_name.setText("Name: " + tvShow.getName());
            tv_tvshow_firstAirDate.setText("First Air Date: " + tvShow.getFirst_air_date());
            tv_tvshow_originalCountry.setText("Countries: " + Arrays.toString(tvShow.getOrigin_country()).replace("\\[|\\]", ""));
            tv_tvshow_voteAverage.setText("Vote average: " + String.valueOf(tvShow.getVote_average()));
            tv_tvshow_seasons.setText(tvShow.getNumber_of_seasons().toString());
            tv_tvshow_episodes.setText(tvShow.getNumber_of_episodes().toString());
            tv_tvshow_episodesDates.setText(tvShow.getLast_air_date());
            tv_tvshow_homepage.setText(tvShow.getHomepage());
            tv_tvshow_originalLanguage.setText(setLanguage(language));
            tv_tvshow_popularity.setText(String.valueOf(tvShow.getPopularity()));
            tv_tvshow_status.setText(tvShow.getStatus());
            tv_tvshow_voteCount.setText(tvShow.getVote_count().toString());
            tv_tvshow_overview.setText(tvShow.getOverview());
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

}