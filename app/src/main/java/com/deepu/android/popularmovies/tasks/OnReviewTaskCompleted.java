package com.deepu.android.popularmovies.tasks;

import com.deepu.android.popularmovies.data.Review;

public interface OnReviewTaskCompleted {
    void onSearchReviewsTaskCompleted(Review[] reviews);
}
