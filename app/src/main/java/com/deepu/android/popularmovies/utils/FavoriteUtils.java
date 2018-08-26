package com.deepu.android.popularmovies.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.deepu.android.popularmovies.data.Movie;
import com.deepu.android.popularmovies.data.MovieContract;

public class FavoriteUtils {

    public static boolean checkIfMovieIsFavorite(Context context, Movie movie) {

        Uri uri = buildMovieSelectedDBUri(movie);

        Cursor cursor = context.getContentResolver().query(uri,
                null,
                "movieId=?",
                new String[]{Integer.toBinaryString(movie.getMovieId())},
                null);

        if (cursor == null) {
            return false;
        } else if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }

        cursor.close();
        return true;
    }

    public static void addFavoriteToDatabase(Context context, Movie movieSelected) {

        ContentValues cv = createMovieContentValuesForDB(movieSelected);
        Uri uri = buildMovieSelectedDBUri(movieSelected);
        context.getContentResolver().insert(uri, cv);
    }

    public static int removeMovieFromFavoritesDB(Context context, Movie movieSelected) {

        Uri uri = buildMovieSelectedDBUri(movieSelected);

        return context.getContentResolver().delete(uri, "movieId=?", new String[]{"id"});


    }

    private static ContentValues createMovieContentValuesForDB(Movie movieSelected) {

        ContentValues cv = new ContentValues();
        cv.put(MovieContract.MovieEntry.MOVIE_ID, Integer.toString(movieSelected.getMovieId()));
        cv.put(MovieContract.MovieEntry.TITLE, movieSelected.getTitle());
        cv.put(MovieContract.MovieEntry.RELEASE_DATE, movieSelected.getReleaseDate());
        cv.put(MovieContract.MovieEntry.SYNOPSIS, movieSelected.getSynopsis());
        cv.put(MovieContract.MovieEntry.VOTE_AVERAGE, Double.toString(movieSelected.getVoteAverage()));
        cv.put(MovieContract.MovieEntry.POSTER_IMAGE, movieSelected.getDbPosterImage());
        return cv;
    }

    public static Uri buildMovieSelectedDBUri(Movie movieSelected) {
        return MovieContract.MovieEntry.CONTENT_URI.buildUpon()
                .appendPath(Integer.toString(movieSelected.getMovieId()))
                .build();
    }
}
