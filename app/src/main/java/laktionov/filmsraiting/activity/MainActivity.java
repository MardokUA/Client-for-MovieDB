package laktionov.filmsraiting.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import laktionov.filmsraiting.R;
import laktionov.filmsraiting.fragment.FavoritesFragment;
import laktionov.filmsraiting.fragment.SearchFragment;
import laktionov.filmsraiting.fragment.PostersFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public final String API_URL_MOVIE_POPULAR = "/movie/popular";
    public final String API_URL_MOVIE_TOP_RATED = "/movie/top_rated";
    public final String API_URL_TVSHOW_POPULAR = "/tv/popular";
    public final String API_URL_TVSHOW_TOP_RATED = "/tv/top_rated";
    public static final String LOG_TAG = "......";

    private FloatingActionButton fab;


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG,"ON RESUME");
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setEnabled(false);
        fab.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SearchFragment fragment;
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_search:
                fragment = new SearchFragment();
                if (getSupportFragmentManager().findFragmentById(R.id.fl_search_holder) != null) {
                    getSupportFragmentManager()
                            .beginTransaction().
                            remove(getSupportFragmentManager().findFragmentById(R.id.fl_search_holder)).commit();
                } else {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.fl_search_holder, fragment)
                            .commit();
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        Bundle args = new Bundle();
        PostersFragment postersFragment = new PostersFragment();
        FragmentManager fr = getSupportFragmentManager();
        switch (item.getItemId()) {
            case R.id.nav_films_popular:
                args.putString("selected_nav", API_URL_MOVIE_POPULAR);
                postersFragment.setArguments(args);

                fr.beginTransaction()
                        .replace(R.id.content_main, postersFragment, "POSTERS_TAG")
                        .addToBackStack("MAIN_ACTIVITY")
                        .commit();
                break;
            case R.id.nav_films_toprated:
                args.putString("selected_nav", API_URL_MOVIE_TOP_RATED);
                postersFragment.setArguments(args);
                fr.beginTransaction()
                        .replace(R.id.content_main, postersFragment, "POSTERS_TAG")
                        .addToBackStack("MAIN_ACTIVITY")
                        .commit();
                break;
            case R.id.nav_tvShow_popular:
                args.putString("selected_nav", API_URL_TVSHOW_POPULAR);
                postersFragment.setArguments(args);
                fr.beginTransaction()
                        .replace(R.id.content_main, postersFragment, "POSTERS_TAG")
                        .addToBackStack("MAIN_ACTIVITY")
                        .commit();
                break;
            case R.id.nav_tvShow_toprated:
                args.putString("selected_nav", API_URL_TVSHOW_TOP_RATED);
                postersFragment.setArguments(args);
                fr.beginTransaction()
                        .replace(R.id.content_main, postersFragment, "POSTERS_TAG")
                        .addToBackStack("MAIN_ACTIVITY")
                        .commit();
                break;

            case R.id.nav_favorites:
                FavoritesFragment favoritesFragment = new FavoritesFragment();

                fr.beginTransaction()
                        .replace(R.id.content_main, favoritesFragment, "FAVORITES_TAG")
                        .addToBackStack("MAIN_ACTIVITY")
                        .commit();
                break;
            case R.id.nav_about:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}

