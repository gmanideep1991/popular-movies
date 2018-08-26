package com.deepu.android.popularmovies.tasks;

import com.deepu.android.popularmovies.data.Movie;

public interface OnTaskCompleted {
    void onSearchMoviesTaskCompleted(Movie[] movies);
}
