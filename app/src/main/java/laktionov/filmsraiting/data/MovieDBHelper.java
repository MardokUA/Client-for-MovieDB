package laktionov.filmsraiting.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MovieDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movieDB";
    public static final String FAVORITES_TABLE_NAME = "favorites";

    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.beginTransaction();
        db.execSQL("CREATE TABLE `" + FAVORITES_TABLE_NAME + "` ("
                + "`_id` INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "`film_id` INTEGER, "
                + "`title` TEXT, "
                + "`film` TEXT, "
                + "`poster_path` TEXT"
                + ");");
        db.setTransactionSuccessful();
        db.endTransaction();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
