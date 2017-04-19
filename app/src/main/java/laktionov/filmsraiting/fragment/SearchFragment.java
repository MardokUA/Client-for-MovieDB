package laktionov.filmsraiting.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import laktionov.filmsraiting.extras.Constans;
import laktionov.filmsraiting.rest.model.Poster;


public class SearchFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, SearchView.OnQueryTextListener {

    private SearchView searchView;
    private Switch aSwitch;

    private List<Poster> searhList;
    private String request;
    private String searchingIn;

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
            searchingIn = Constans.TVSHOW;

        } else {
            aSwitch.setText("Movie");
            searchingIn = Constans.MOVIE;
        }
    }

    @Override
    public boolean onQueryTextSubmit(String request) {
        this.request = request;
        Bundle args = new Bundle();
        args.putString(Constans.SEARCH_REQUEST, this.request);
        args.putString(Constans.SEARCHING_IN, searchingIn);

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
                .addToBackStack(Constans.SEARCH_FRAGMENT_TO_BACKSTACK)
                .commit();

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

}
