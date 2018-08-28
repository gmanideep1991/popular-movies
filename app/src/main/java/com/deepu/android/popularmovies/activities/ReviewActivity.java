package com.deepu.android.popularmovies.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.deepu.android.popularmovies.BuildConfig;
import com.deepu.android.popularmovies.R;
import com.deepu.android.popularmovies.adapters.ReviewAdapter;
import com.deepu.android.popularmovies.data.Review;
import com.deepu.android.popularmovies.tasks.GetReviewsAsyncTask;
import com.deepu.android.popularmovies.tasks.OnReviewTaskCompleted;

import static com.deepu.android.popularmovies.utils.NetworkUtils.isInternetAvailable;

public class ReviewActivity extends AppCompatActivity {

    private LinearLayoutManager reviewLayoutManager;
    private int movieId;
    private RecyclerView reviewsView;
    private final String RECYCLER_POSITION_KEY = "recycler_position";
    private int mPosition = RecyclerView.NO_POSITION;
    private static Bundle mBundleState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        Intent intent = getIntent();
        movieId = intent.getIntExtra(getString(R.string.movie_id),0);
        populateReviews(movieId);
    }

    private void populateReviews(int movieId) {

        if (isInternetAvailable(this)) {
            reviewsView = findViewById(R.id.reviews_recycler_view);
            reviewLayoutManager = new LinearLayoutManager(this);
            reviewsView.setLayoutManager(reviewLayoutManager);
            DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(reviewsView.getContext(),
                    reviewLayoutManager.getOrientation());
            reviewsView.addItemDecoration(mDividerItemDecoration);

            String apiKey = BuildConfig.ApiKey;
            OnReviewTaskCompleted reviewTaskCompleted = new OnReviewTaskCompleted() {
                @Override
                public void onSearchReviewsTaskCompleted(Review[] reviews) {
                    reviewsView.setAdapter(new ReviewAdapter(reviews));
                }
            };
            GetReviewsAsyncTask getReviewsAsyncTask = new GetReviewsAsyncTask(apiKey, reviewTaskCompleted);
            getReviewsAsyncTask.execute(Integer.toString(movieId));
        } else {
            Toast.makeText(this,getString(R.string.network_unavailable),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBundleState = new Bundle();
        mPosition = reviewLayoutManager.findFirstCompletelyVisibleItemPosition();
        mBundleState.putInt(RECYCLER_POSITION_KEY, mPosition);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(RECYCLER_POSITION_KEY,  reviewLayoutManager.findFirstCompletelyVisibleItemPosition());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        if (state.containsKey(RECYCLER_POSITION_KEY)) {
            mPosition = state.getInt(RECYCLER_POSITION_KEY);
            if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
            // Scroll the RecyclerView to mPosition
            reviewsView.scrollToPosition(mPosition);
        }

        super.onRestoreInstanceState(state);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBundleState != null) {
            mPosition = mBundleState.getInt(RECYCLER_POSITION_KEY);
            if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
            reviewsView.scrollToPosition(mPosition);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
