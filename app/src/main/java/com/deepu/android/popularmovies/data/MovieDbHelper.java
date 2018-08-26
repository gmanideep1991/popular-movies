package com.deepu.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.deepu.android.popularmovies.data.MovieContract.MovieEntry;

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "popularMovies.db";
    private static final int DATABASE_VERSION = 3;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_POPULAR_MOVIES_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " ("
                + MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MovieEntry.TITLE + " TEXT NOT NULL, "
                + MovieEntry.MOVIE_ID + " INT NOT NULL, "
                + MovieEntry.RELEASE_DATE + " TEXT, "
                + MovieEntry.VOTE_AVERAGE + " TEXT, "
                + MovieEntry.SYNOPSIS + " TEXT, "
                + MovieEntry.POSTER_IMAGE + " BLOB "
                + ");";

        sqLiteDatabase.execSQL(SQL_CREATE_POPULAR_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
