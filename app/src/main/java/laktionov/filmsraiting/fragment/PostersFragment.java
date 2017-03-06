package laktionov.filmsraiting.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import laktionov.filmsraiting.BuildConfig;
import laktionov.filmsraiting.R;
import laktionov.filmsraiting.adapter.MovieListAdapter;
import laktionov.filmsraiting.model.Poster;


public class PostersFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, AbsListView.OnScrollListener {

    private static final String TAG = "PostersFragment";

    private Integer currentPage = 1;
    private String navItemSelected;
    private GridView lvPosters;
    private List<Poster> posterList;
    private MovieListAdapter movieListAdapter;
    private FloatingActionButton fab;
    private String search;
    private String request;
    private int total_pages;
    private int total_results;

    public PostersFragment() {
        this.posterList = new ArrayList<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navItemSelected = getArguments().getString("selected_nav");
        search = getArguments().getString("url");
        request = getArguments().getString("request");
        movieListAdapter = new MovieListAdapter(getContext(), posterList);

        new DownloadPostersAsyncTask().execute(currentPage);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_posters, container, false);

        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setImageResource(R.drawable.arrow_up);
        fab.setVisibility(View.VISIBLE);
        fab.setEnabled(true);
        fab.setOnClickListener(this);


        lvPosters = (GridView) root.findViewById(R.id.lv_posters);
        lvPosters.setOnItemClickListener(this);
        lvPosters.setOnScrollListener(this);
        lvPosters.setAdapter(movieListAdapter);


        return root;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                lvPosters.smoothScrollToPositionFromTop(0, 0, 100);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Bundle args = new Bundle();
        Fragment fragment = null;
        // Switch base on title and name. Films has a "title" field instead "name" and backward
        if (posterList.get(position).getOriginal_name() == null) { // Film selected...
            fragment = new MovieDetailsFragment();
            args.putLong("film_id", posterList.get(position).getId());
            fragment.setArguments(args);
        }
        if (posterList.get(position).getOriginal_title() == null) { // TVShow selected...
            fragment = new TVShowDetailsFragment();
            args.putLong("tvshow_id", posterList.get(position).getId());
            fragment.setArguments(args);
        }

        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                .replace(R.id.content_main, fragment)
                .addToBackStack("POSTER_FRAGMENT")
                .commit();
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        Log.d(TAG, firstVisibleItem + " + " + visibleItemCount + " = " + totalItemCount);
        if (firstVisibleItem + visibleItemCount == totalItemCount && firstVisibleItem != 0) {
            currentPage++;
            new DownloadPostersAsyncTask().execute(currentPage);
        }
    }

    private class DownloadPostersAsyncTask extends AsyncTask<Integer, Poster, Void> {

        private static final String API_URL_BASE = "https://api.themoviedb.org/3";
        private Gson gson;

        public DownloadPostersAsyncTask() {
            gson = new Gson();
        }

        @Override
        protected Void doInBackground(Integer... pages) {

            for (Integer pageNumber : pages)
                try {
                    URL url;
                    if (search != null && request != null) {
                        url = new URL(search + "&language=en-US&page=" + pageNumber + "?&query=" + request);
                    } else {
                        url = new URL(API_URL_BASE + navItemSelected + "?api_key=" + BuildConfig.THE_MOVIEDB_API_KEY + "&language=en-US&page=" + pageNumber);
                    }
                    JsonObject jsonObject = gson.fromJson(IOUtils.toString(url.openStream()), JsonObject.class);

                    int page = jsonObject.get("page").getAsInt();
                    total_results = jsonObject.get("total_results").getAsInt();
                    total_pages = jsonObject.get("page").getAsInt();

                    JsonArray results = jsonObject.getAsJsonArray("results");
                    Log.d(TAG, "TOTAL RESULTS = " + total_results);
                    Log.d(TAG, "TOTAL PAGES = " + total_pages);

                    if (total_results == 0) {
                        EmptyFragment emptyFragment = new EmptyFragment();
                        Log.d(TAG, "METHOD IN");
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .remove(getFragmentManager().findFragmentById(R.id.content_main))
                                .commit();

                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content_main, emptyFragment)
                                .commit();
                    } else {
                        for (JsonElement elm : results) {

                            Poster poster = gson.fromJson(elm.toString(), Poster.class);

                            publishProgress(poster);
                        }
                    }


                } catch (JsonSyntaxException | IOException e) {
                    e.printStackTrace();
                }
            return null;
        }


        @Override
        protected void onProgressUpdate(Poster... values) {
            posterList.addAll(Arrays.asList(values));
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            movieListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDetach() {
        fab.setEnabled(false);
        fab.setVisibility(View.INVISIBLE);
        super.onDetach();
    }
}