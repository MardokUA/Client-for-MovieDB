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
import laktionov.filmsraiting.extras.Constans;
import laktionov.filmsraiting.rest.BaseApi;
import laktionov.filmsraiting.rest.model.MovieResponse;
import laktionov.filmsraiting.rest.model.Poster;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class PostersFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, AbsListView.OnScrollListener {

    private static final String TAG = "PostersFragment";

    private Retrofit retrofit;
    private BaseApi baseApi;
    private Integer currentPage = 1;
    private String menuItemSelectedInDrawer;
    private String category;
    private GridView lvPosters;
    private List<Poster> posterList;
    private MovieListAdapter movieListAdapter;
    private FloatingActionButton fab;

    private String searchingIn;
    private String request;
    private int total_pages;
    private int total_results;

    public PostersFragment() {
        this.posterList = new ArrayList<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        menuItemSelectedInDrawer = getArguments().getString(Constans.MENU_ITEM_SELECTED_IN_DRAWER);
        category = getArguments().getString(Constans.CATEGORY_SELECTED_IN_DRAWER);

        request = getArguments().getString(Constans.SEARCH_REQUEST);
        searchingIn = getArguments().getString(Constans.SEARCHING_IN);

        movieListAdapter = new MovieListAdapter(getContext(), posterList);

        //Logger for Retrofit
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BaseApi.API_URL_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        baseApi = retrofit.create(BaseApi.class);

        if (request != null) {
            getSearchingFilmsPosters(currentPage);
        } else {
            getCategoryFilmsPosters(currentPage);
        }

    }

    private void getSearchingFilmsPosters(Integer currentPage) {
        Call<MovieResponse> getSearchingPoster;
        getSearchingPoster = baseApi.searchFilms(searchingIn, String.valueOf(currentPage), request);
        getSearchingPoster.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.body().getTotal_results() == 0) {

                    EmptyFragment emptyFragment = new EmptyFragment();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .remove(getFragmentManager().findFragmentById(R.id.content_main))
                            .commit();

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_main, emptyFragment)
                            .commit();

                } else {
                    total_pages = response.body().getTotal_pages();
                    posterList.addAll(response.body().getPosterList());
                    movieListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {

            }
        });
    }

    private void getCategoryFilmsPosters(Integer currentPage) {
        Call<MovieResponse> getPosters = null;
        switch (category) {
            case Constans.POPULAR:
                getPosters = baseApi.getPopular(menuItemSelectedInDrawer, String.valueOf(currentPage));
                break;
            case Constans.TOP_RATED:
                getPosters = baseApi.getTopRated(menuItemSelectedInDrawer, String.valueOf(currentPage));
                break;
        }
        getPosters.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                posterList.addAll(response.body().getPosterList());
                movieListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {

            }
        });
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
        if (firstVisibleItem + visibleItemCount == totalItemCount && firstVisibleItem != 0 && currentPage < total_pages) {
            currentPage++;
            if (request != null) {
                getSearchingFilmsPosters(currentPage);
            } else {
                getCategoryFilmsPosters(currentPage);
            }

//            new DownloadPostersAsyncTask().execute(currentPage);
        }
    }

    private class DownloadPostersAsyncTask extends AsyncTask<Integer, Poster, Void> {

        private Gson gson;

        public DownloadPostersAsyncTask() {
            gson = new Gson();
        }

        @Override
        protected Void doInBackground(Integer... pages) {

            for (Integer pageNumber : pages)
                try {
                    URL url;
                    if (searchingIn != null && request != null) {
                        url = new URL(searchingIn + "&language=en-US&page=" + pageNumber + "?&query=" + request);
                    } else {
                        url = new URL(BaseApi.API_URL_BASE + menuItemSelectedInDrawer + "?api_key=" + BuildConfig.THE_MOVIEDB_API_KEY + "&language=en-US&page=" + pageNumber);
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