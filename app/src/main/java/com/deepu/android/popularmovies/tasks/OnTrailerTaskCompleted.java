package com.deepu.android.popularmovies.tasks;

import com.deepu.android.popularmovies.data.Trailer;

public interface OnTrailerTaskCompleted {
    void onSearchTrailersTaskCompleted(Trailer[] trailers);
}
