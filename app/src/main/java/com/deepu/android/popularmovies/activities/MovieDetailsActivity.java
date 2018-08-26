package com.deepu.android.popularmovies.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.deepu.android.popularmovies.BuildConfig;
import com.deepu.android.popularmovies.R;
import com.deepu.android.popularmovies.adapters.ReviewAdapter;
import com.deepu.android.popularmovies.adapters.TrailerAdapter;
import com.deepu.android.popularmovies.data.Movie;
import com.deepu.android.popularmovies.data.Review;
import com.deepu.android.popularmovies.data.Trailer;
import com.deepu.android.popularmovies.tasks.GetReviewsAsyncTask;
import com.deepu.android.popularmovies.tasks.GetTrailersAsyncTask;
import com.deepu.android.popularmovies.tasks.OnReviewTaskCompleted;
import com.deepu.android.popularmovies.tasks.OnTrailerTaskCompleted;
import com.deepu.android.popularmovies.utils.FavoriteUtils;
import com.deepu.android.popularmovies.utils.ImageUtils;

import static com.deepu.android.popularmovies.utils.NetworkUtils.isInternetAvailable;

public class MovieDetailsActivity extends AppCompatActivity {

    private boolean isFavoriteMovie;
    private Movie currentMovie;
    ImageView poster;
    Button favoriteButton;
    TextView trailersHeading;
    TextView reviewsHeading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        Intent intent = getIntent();
        currentMovie = intent.getParcelableExtra(getString(R.string.parcel_movie));

        poster = findViewById(R.id.poster);
        TextView title = findViewById(R.id.movie_title);
        TextView releaseDate = findViewById(R.id.release_date);
        TextView voteAverage = findViewById(R.id.vote_average);
        TextView synopsis = findViewById(R.id.synopsis);
        favoriteButton = (Button) findViewById(R.id.favorite_button);
        trailersHeading = findViewById(R.id.trailer_heading);
        reviewsHeading = findViewById(R.id.review_heading);
        title.setText(currentMovie.getTitle());

        ImageUtils.setImageView(currentMovie, poster);

        synopsis.setText(currentMovie.getSynopsis());
        voteAverage.setText(currentMovie.getRating());
        releaseDate.setText(currentMovie.getReleaseDate());
        getOtherItems(currentMovie);

    }

    public void addToFavorites(View view) {
        if (isFavoriteMovie) {
            if (FavoriteUtils.removeMovieFromFavoritesDB(this, currentMovie) == 1) {
                isFavoriteMovie = false;
            }
            favoriteButton.setText(R.string.favorite);
            showMessage("Removed from favorites");
        } else {
            FavoriteUtils.addFavoriteToDatabase(this, currentMovie);
            isFavoriteMovie = true;
            favoriteButton.setText(R.string.unfavorite);
            showMessage("Added to favorites");
        }
    }

    private void populateReviews(int movieId) {

        if (isInternetAvailable(this)) {
            final RecyclerView reviewsView = findViewById(R.id.reviews_recycler_view);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            reviewsView.setLayoutManager(layoutManager);
            String apiKey = BuildConfig.ApiKey;
            OnReviewTaskCompleted reviewTaskCompleted = new OnReviewTaskCompleted() {
                @Override
                public void onSearchReviewsTaskCompleted(Review[] reviews) {
                    if(reviews.length ==0){
                    reviewsHeading.setVisibility(View.GONE);
                    }
                    reviewsView.setAdapter(new ReviewAdapter(reviews));
                }
            };
            GetReviewsAsyncTask getReviewsAsyncTask = new GetReviewsAsyncTask(apiKey, reviewTaskCompleted);
            getReviewsAsyncTask.execute(Integer.toString(movieId));
        } else {
            showMessage(getString(R.string.network_unavilable));
        }
    }

    private void getTrailers(int movieId, final Context context) {
        if (isInternetAvailable(this)) {
            final RecyclerView trailersView = findViewById(R.id.trailers_recycler_view);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            trailersView.setLayoutManager(layoutManager);

            String apiKey = BuildConfig.ApiKey;
            OnTrailerTaskCompleted trailerTaskCompleted = new OnTrailerTaskCompleted() {
                @Override
                public void onSearchTrailersTaskCompleted(Trailer[] trailers) {
                    if(trailers.length ==0){
                        trailersHeading.setVisibility(View.GONE);
                    }
                    trailersView.setAdapter(new TrailerAdapter(trailers,context));
                }
            };
            GetTrailersAsyncTask getTrailersAsyncTask = new GetTrailersAsyncTask(apiKey, trailerTaskCompleted);
            getTrailersAsyncTask.execute(Integer.toString(movieId));
        } else {
            showMessage(getString(R.string.network_unavilable));
        }
    }

    private void getOtherItems(Movie movie) {
        isFavoriteMovie = FavoriteUtils.checkIfMovieIsFavorite(this, movie);
        getTrailers(movie.getMovieId(),this);
        populateReviews(movie.getMovieId());
        if(isFavoriteMovie){
           favoriteButton.setText(R.string.unfavorite);
        }
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
