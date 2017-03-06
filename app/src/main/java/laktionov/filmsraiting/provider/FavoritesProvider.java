package laktionov.filmsraiting.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.util.Log;

import laktionov.filmsraiting.data.MovieDBHelper;


public class FavoritesProvider extends ContentProvider {

    public static final String LOG_TAG = "URI";

    private static final int URI_FAVORITES = 1;
    private static final int URI_FAVORITES_ID = 2;
    private static final int URI_FAVORITES_FILM_ID = 3;

    public static final String AUTHORITY = "laktionov.filmsraiting.provider";
    public static final String FAVORITES_PATH = FilmsContract.Favorite.TABLE_NAME;

    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);
    public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI, FAVORITES_PATH);

    public static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(AUTHORITY, FAVORITES_PATH, URI_FAVORITES);
        URI_MATCHER.addURI(AUTHORITY, FAVORITES_PATH + "/#", URI_FAVORITES_ID);
        URI_MATCHER.addURI(AUTHORITY, FAVORITES_PATH + "/film_id/#", URI_FAVORITES_FILM_ID);
        Log.d(LOG_TAG, "URI = " + Uri.parse(FAVORITES_PATH + "/film_id"));
    }

    private MovieDBHelper dbh;

    public FavoritesProvider() {
    }

    @Override
    public boolean onCreate() {
        this.dbh = new MovieDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbh.getWritableDatabase();

        switch (URI_MATCHER.match(uri)) {
            case URI_FAVORITES:

                Cursor query = db.query(FAVORITES_PATH, projection, selection, selectionArgs, null, null, sortOrder);
                query.setNotificationUri(getContext().getContentResolver(), CONTENT_URI);

                return query;
            case URI_FAVORITES_ID:

                String id = uri.getLastPathSegment();

                return db.query(FAVORITES_PATH, projection, FilmsContract.Favorite._ID + " = ?",
                        new String[]{id}, null, null, sortOrder);
        }
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (URI_MATCHER.match(uri)) {
            case URI_FAVORITES:
                SQLiteDatabase db = dbh.getWritableDatabase();

                db.beginTransaction();
                db.insert(FAVORITES_PATH, "_id", values);
                db.setTransactionSuccessful();
                db.endTransaction();

                db.close();
                break;
        }

        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (URI_MATCHER.match(uri)) {
            case URI_FAVORITES:
                SQLiteDatabase db = dbh.getWritableDatabase();

                db.beginTransaction();
                int deleted = db.delete(FAVORITES_PATH, selection, selectionArgs);
                db.setTransactionSuccessful();
                db.endTransaction();
                db.close();
                return deleted;
        }
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
