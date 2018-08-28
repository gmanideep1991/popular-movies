package com.deepu.android.popularmovies.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.deepu.android.popularmovies.BuildConfig;
import com.deepu.android.popularmovies.R;
import com.deepu.android.popularmovies.adapters.TrailerAdapter;
import com.deepu.android.popularmovies.data.Trailer;
import com.deepu.android.popularmovies.tasks.GetTrailersAsyncTask;
import com.deepu.android.popularmovies.tasks.OnTrailerTaskCompleted;

import static com.deepu.android.popularmovies.utils.NetworkUtils.isInternetAvailable;

public class TrailerActivity extends AppCompatActivity {

    private int movieId;
    private LinearLayoutManager trailerLayoutManager;
    private RecyclerView trailersView;
    private final String RECYCLER_POSITION_KEY = "recycler_position";
    private int mPosition = RecyclerView.NO_POSITION;
    private static Bundle mBundleState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trailer);
        Intent intent = getIntent();
        movieId = intent.getIntExtra(getString(R.string.movie_id),0);
        populateTrailers(this);

    }

    private void populateTrailers(final Context context){
        if (isInternetAvailable(this)) {
            trailersView = findViewById(R.id.trailers_recycler_view);
            trailerLayoutManager = new LinearLayoutManager(this);
            trailersView.setLayoutManager(trailerLayoutManager);


            String apiKey = BuildConfig.ApiKey;
            OnTrailerTaskCompleted trailerTaskCompleted = new OnTrailerTaskCompleted() {
                @Override
                public void onSearchTrailersTaskCompleted(Trailer[] trailers) {
                    trailersView.setAdapter(new TrailerAdapter(trailers,context));
                }
            };
            GetTrailersAsyncTask getTrailersAsyncTask = new GetTrailersAsyncTask(apiKey, trailerTaskCompleted);
            getTrailersAsyncTask.execute(Integer.toString(movieId));
        } else {
            Toast.makeText(this,getString(R.string.network_unavailable),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBundleState = new Bundle();
        mPosition = trailerLayoutManager.findFirstCompletelyVisibleItemPosition();
        mBundleState.putInt(RECYCLER_POSITION_KEY, mPosition);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(RECYCLER_POSITION_KEY,  trailerLayoutManager.findFirstCompletelyVisibleItemPosition());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        if (state.containsKey(RECYCLER_POSITION_KEY)) {
            mPosition = state.getInt(RECYCLER_POSITION_KEY);
            if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
            // Scroll the RecyclerView to mPosition
            trailersView.scrollToPosition(mPosition);
        }

        super.onRestoreInstanceState(state);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBundleState != null) {
            mPosition = mBundleState.getInt(RECYCLER_POSITION_KEY);
            if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
            trailersView.scrollToPosition(mPosition);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
