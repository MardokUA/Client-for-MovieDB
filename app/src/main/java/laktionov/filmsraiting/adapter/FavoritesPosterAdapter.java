package laktionov.filmsraiting.adapter;


import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import laktionov.filmsraiting.R;
import laktionov.filmsraiting.fragment.MovieDetailsFragment;
import laktionov.filmsraiting.fragment.TVShowDetailsFragment;
import laktionov.filmsraiting.provider.FavoritesProvider;
import laktionov.filmsraiting.provider.FilmsContract;

public class FavoritesPosterAdapter extends CursorAdapter
        implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {

    public static final String LOG_TAG = "TAG";

    private LayoutInflater inflater;
    private List<View> items;
    private Context context;

    private Set<Long> selectedItems;

    public Set<Long> getSelectedItems() {
        return selectedItems;
    }

    public FavoritesPosterAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.context = context;
        this.items = new ArrayList<>();
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.selectedItems = new HashSet<>();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = inflater.inflate(R.layout.fragment_poster_list_item, parent, false);
        items.add(view);

        return view;
    }

    @Override
    public void bindView(View convertView, Context context, Cursor cursor) {

        long id = cursor.getLong(cursor.getColumnIndex(FilmsContract.Favorite._ID));

        for (Long sid : selectedItems) {
            if (id == sid) {
                convertView.setAlpha(1.f);
            } else {
                convertView.setAlpha(.8f);
            }
        }

        ImageView lvPoster = (ImageView) convertView.findViewById(R.id.posters_list_item_iv_poster);
        TextView tvFilmTitle = (TextView) convertView.findViewById(R.id.posters_list_item_tv_title);


        String image_path = MovieListAdapter.API_URL_IMAGE + cursor.getString(cursor.getColumnIndex(
                FilmsContract.Favorite.POSTER_PATH));
        Log.d(LOG_TAG,"IMAGE PATH" + image_path);

        if (image_path.contains("null")) {
            lvPoster.setImageResource(R.drawable.no_image_avavailable);
        } else {
            Picasso.with(context).load(image_path)
                    .into(lvPoster);
        }
        String title = cursor.getString(cursor.getColumnIndex(FilmsContract.Favorite.TITLE));

        tvFilmTitle.setText(title);

    }

    private boolean editMode;


    public void turnEditMode(View target, boolean flag) {
        editMode = flag;

        if (editMode) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.edit_mode_on);
            animation.setDuration(150);
            animation.setFillAfter(true);

            Animation animation_selected = AnimationUtils.loadAnimation(context, R.anim.edit_mode_on_selected);
            animation_selected.setDuration(150);
            animation_selected.setFillAfter(true);

            for (View v : items) {
                if (target.equals(v)) {
                    v.startAnimation(animation_selected);
                } else {
                    v.startAnimation(animation);
                }
            }
        } else {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.edit_mode_off);
            animation.setDuration(150);
            animation.setFillAfter(true);

            for (View v : items) {
                v.startAnimation(animation);
            }
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(LOG_TAG, "ID = " + id);

        if (selectedItems.isEmpty()) {
            selectedItems.add(id);
            turnEditMode(view, true);
        } else {
            selectedItems.clear();
            turnEditMode(view, false);
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (editMode) {
            Animation animation;

            if (selectedItems.contains(id)) {
                selectedItems.remove(id);

                animation = AnimationUtils.loadAnimation(context, R.anim.edit_mode_on);
                animation.setDuration(150);
                animation.setFillAfter(true);
            } else {
                animation = AnimationUtils.loadAnimation(context, R.anim.edit_mode_on_selected);
                animation.setDuration(150);
                animation.setFillAfter(true);

                selectedItems.add(id);
            }

            view.startAnimation(animation);
        } else {
            Uri quri = ContentUris.withAppendedId(FavoritesProvider.CONTENT_URI, id);

            Cursor cursor = context.getContentResolver().query(quri, null, null, null, null);

            if (!cursor.moveToFirst()) {
                return;
            }

            Fragment fragment = null;
            Bundle args = new Bundle();
            args.putBoolean("inFavorites", true);

            if (cursor.getString(cursor.getColumnIndex("film")).equals("movie")) {
                fragment = new MovieDetailsFragment();
                args.putLong("film_id", cursor.getLong(cursor.getColumnIndex("film_id")));
                fragment.setArguments(args);
            }
            if (cursor.getString(cursor.getColumnIndex("film")).equals("tvshow")) {
                fragment = new TVShowDetailsFragment();
                args.putLong("tvshow_id", cursor.getLong(cursor.getColumnIndex("film_id")));
                fragment.setArguments(args);
            }

            FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
            transaction.replace(R.id.content_main, fragment);
            transaction.addToBackStack("FAVORITES");
            transaction.commit();

        }

    }
}
