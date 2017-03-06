package laktionov.filmsraiting.activity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import laktionov.filmsraiting.R;
import laktionov.filmsraiting.data.MovieDBHelper;

import static laktionov.filmsraiting.data.MovieDBHelper.DATABASE_NAME;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button btnDrop = (Button) findViewById(R.id.btn_drop);
        btnDrop.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        MovieDBHelper dbh = new MovieDBHelper(this);
        SQLiteDatabase db = dbh.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
    }
}
