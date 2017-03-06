package laktionov.filmsraiting.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SearchView;
import android.widget.Switch;

import java.util.ArrayList;

import java.util.List;

import laktionov.filmsraiting.BuildConfig;
import laktionov.filmsraiting.R;
import laktionov.filmsraiting.adapter.MovieListAdapter;
import laktionov.filmsraiting.model.Poster;


public class SearchFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, SearchView.OnQueryTextListener {

    public static final String TAG = "LOG";

    private List<Poster> searhList;
    private MovieListAdapter adapter;
    private static final String API_URL_BASE = "https://api.themoviedb.org/3";
    private static final String SEARCH_TV_SHOW = API_URL_BASE + "/search/tv?api_key=" + BuildConfig.THE_MOVIEDB_API_KEY;
    private static final String SEARCH_MOVIE = API_URL_BASE + "/search/movie?api_key=" + BuildConfig.THE_MOVIEDB_API_KEY;
    private String searching = API_URL_BASE + "/search/movie?api_key=" + BuildConfig.THE_MOVIEDB_API_KEY;
    ;
    private String request;


    SearchView searchView;
    Switch aSwitch;

    public SearchFragment() {
        this.searhList = new ArrayList<>();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_search, container, false);
        Log.d(TAG, "FROM SEARCH");

        searchView = (SearchView) root.findViewById(R.id.sv_search);
        aSwitch = (Switch) root.findViewById(R.id.sw_switcher);

        searchView.setIconifiedByDefault(false);
        searchView.setQuery("", false);
        searchView.setOnQueryTextListener(this);

        aSwitch.setChecked(false);
        aSwitch.setOnCheckedChangeListener(this);

        return root;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            aSwitch.setText("TV Show");
            searching = SEARCH_TV_SHOW;
            Log.d(TAG, "SEARCHING IN : " + searching);

        } else {
            aSwitch.setText("Movie");
            searching = SEARCH_MOVIE;
            Log.d(TAG, "SEARCHING IN : " + searching);

        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        request = query;
        Log.d(TAG, "URL = " + searching);
        Bundle args = new Bundle();
        args.putString("url", searching);
        args.putString("request", request);

        searchView.clearFocus();

        PostersFragment fragment = new PostersFragment();
        fragment.setArguments(args);

        if (getActivity().getSupportFragmentManager().findFragmentById(R.id.content_main) != null) {

            getFragmentManager().beginTransaction()
                    .remove(getFragmentManager().findFragmentById(R.id.content_main))
                    .commit();
        }
        getFragmentManager().beginTransaction()
                .remove(getFragmentManager().findFragmentById(R.id.fl_search_holder))
                .commit();

        getFragmentManager().beginTransaction()
                .replace(R.id.content_main, fragment)
                .addToBackStack("SEARCH")
                .commit();

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

}
