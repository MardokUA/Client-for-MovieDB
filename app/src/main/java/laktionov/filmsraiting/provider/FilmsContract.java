package laktionov.filmsraiting.provider;

import android.provider.BaseColumns;

import laktionov.filmsraiting.data.MovieDBHelper;

public final class FilmsContract {

    public final class Favorite implements BaseColumns {

        public static final String TABLE_NAME = MovieDBHelper.FAVORITES_TABLE_NAME;

        public static final String FILM_ID = "film_id";
        public static final String TITLE = "title";
        public static final String FILM = "film";
        public static final String POSTER_PATH = "poster_path";
    }


}