package laktionov.filmsraiting.fragment;


import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import java.util.Set;

import laktionov.filmsraiting.R;
import laktionov.filmsraiting.adapter.FavoritesPosterAdapter;

import laktionov.filmsraiting.provider.FavoritesProvider;
import laktionov.filmsraiting.provider.FilmsContract;

public class FavoritesFragment extends Fragment implements View.OnClickListener {
    public static final String LOG = ".....";

    private FloatingActionButton fab;
    private FavoritesPosterAdapter fAdapter;
    private GridView lvFavoritesList;

    public FavoritesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_favorites, container, false);

        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setImageResource(android.R.drawable.ic_delete);
        fab.setVisibility(View.VISIBLE);
        fab.setEnabled(true);
        fab.setOnClickListener(this);

        lvFavoritesList = (GridView) root.findViewById(R.id.lvFavoritesList);

        Cursor cursor = getContext().getContentResolver()
                .query(FavoritesProvider.CONTENT_URI, null, null, null, null);

        this.fAdapter = new FavoritesPosterAdapter(getContext(), cursor, 0);
        lvFavoritesList.setAdapter(fAdapter);
        lvFavoritesList.setOnItemLongClickListener(fAdapter);
        lvFavoritesList.setOnItemClickListener(fAdapter);

        return root;
    }

    @Override
    public void onClick(View v) {

        Set<Long> items = fAdapter.getSelectedItems();
        if (items == null) {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                Toast.makeText(getActivity(), "Nothing is choose!", Toast.LENGTH_SHORT).show();
            } else {
                Snackbar.make(v, "Nothing is choose!", Snackbar.LENGTH_SHORT).show();
            }
        } else {
            Object[] selection = items.toArray();
            String[] selectionArgs = new String[selection.length];
            for (int i = 0; i < selectionArgs.length; i++) {
                selectionArgs[i] = selection[i].toString();
            }
            int deleted = 0;
            for (String entity : selectionArgs) {
                getContext().getContentResolver().delete(FavoritesProvider.CONTENT_URI,
                        FilmsContract.Favorite._ID + " = ?", new String[]{entity});

                deleted++;
            }

            fAdapter.notifyDataSetChanged();

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                Toast.makeText(getActivity(), deleted + " films deleted", Toast.LENGTH_SHORT).show();
            } else {
                Snackbar.make(v, deleted + " films deleted", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDetach() {
        fab.setEnabled(false);
        fab.setVisibility(View.INVISIBLE);
        super.onDetach();
    }

}
