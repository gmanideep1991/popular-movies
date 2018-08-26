package com.deepu.android.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {

    public static String AUTHORITY = "com.deepu.android.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_FAVORITE_MOVIES = "favoritemovies";

    private MovieContract() {
    }

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITE_MOVIES)
                .build();

        public static final String TABLE_NAME = "favoritemovies";
        public static final String TITLE = "title";
        public static final String MOVIE_ID = "movieId";
        public static final String SYNOPSIS = "synopsis";
        public static final String RELEASE_DATE = "releaseDate";
        public static final String VOTE_AVERAGE = "voteAverage";
        public static final String POSTER_IMAGE = "posterImage";
    }
}
