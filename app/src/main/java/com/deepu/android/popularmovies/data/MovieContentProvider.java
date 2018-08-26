package com.deepu.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class MovieContentProvider extends ContentProvider {

    private static final int CODE_FAVORITE_MOVIES = 100;
    private static final int CODE_FAVORITE_MOVIE_WITH_ID = 101;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final String ID_SELECTION_PARAMETER = "movieId=?";

    private MovieDbHelper movieDbHelper;

    public static UriMatcher buildUriMatcher() {

        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MovieContract.AUTHORITY,
                MovieContract.PATH_FAVORITE_MOVIES,
                CODE_FAVORITE_MOVIES);

        uriMatcher.addURI(MovieContract.AUTHORITY,
                MovieContract.PATH_FAVORITE_MOVIES + "/#",
                CODE_FAVORITE_MOVIE_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        movieDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = movieDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);

        Cursor returnCursor = null;

        switch (match) {

            case CODE_FAVORITE_MOVIES:
                returnCursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case CODE_FAVORITE_MOVIE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                String[] mSelectionArgs = new String[]{id};

                returnCursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        ID_SELECTION_PARAMETER,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = sUriMatcher.match(uri);

        switch (match) {
            case CODE_FAVORITE_MOVIES:
                return "vnd.android.cursor.dir" + "/" +
                        MovieContract.AUTHORITY + "/" +
                        MovieContract.PATH_FAVORITE_MOVIES;
            case CODE_FAVORITE_MOVIE_WITH_ID:
                return "vnd.android.cursor.item" + "/" +
                        MovieContract.AUTHORITY + "/" +
                        MovieContract.PATH_FAVORITE_MOVIES;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri;
        long _id;

        switch (match) {
            case CODE_FAVORITE_MOVIES:
                _id = db.insert(MovieContract.MovieEntry.TABLE_NAME,
                        null,
                        contentValues);
                if (_id > 0) {
                    returnUri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, _id);
                } else {
                    throw new android.database.SQLException("Failed to insert row: " + _id);
                }
                break;
            case CODE_FAVORITE_MOVIE_WITH_ID:

                _id = db.insert(MovieContract.MovieEntry.TABLE_NAME,
                        null,
                        contentValues);

                if (_id > 0) {
                    returnUri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, _id);
                } else {
                    throw new android.database.SQLException("Failed to insert row: " + _id);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        int numOfMoviesDeleted;

        // To delete entire table
        if (null == selection) selection = "1";

        switch (match) {
            case CODE_FAVORITE_MOVIES:
                numOfMoviesDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME,
                        selection,
                        selectionArgs);

            case CODE_FAVORITE_MOVIE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                numOfMoviesDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME,
                        ID_SELECTION_PARAMETER,
                        new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        if (numOfMoviesDeleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numOfMoviesDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();

        int moviesUpdated;
        int match = sUriMatcher.match(uri);

        switch (match) {
            case CODE_FAVORITE_MOVIE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                moviesUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME,
                        contentValues,
                        ID_SELECTION_PARAMETER,
                        new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (moviesUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return moviesUpdated;
    }
}
