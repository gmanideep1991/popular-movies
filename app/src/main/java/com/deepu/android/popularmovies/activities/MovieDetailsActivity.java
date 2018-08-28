package com.deepu.android.popularmovies.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.deepu.android.popularmovies.R;
import com.deepu.android.popularmovies.data.Movie;
import com.deepu.android.popularmovies.utils.FavoriteUtils;
import com.deepu.android.popularmovies.utils.ImageUtils;

public class MovieDetailsActivity extends AppCompatActivity {

    private boolean isFavoriteMovie;
    private Movie currentMovie;
    ImageView poster;
    Button favoriteButton;

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
        title.setText(currentMovie.getTitle());

        ImageUtils.setImageView(currentMovie, poster);

        synopsis.setText(currentMovie.getSynopsis());
        voteAverage.setText(currentMovie.getRating());
        releaseDate.setText(currentMovie.getReleaseDate());
        getOtherItems(currentMovie);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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

    public void showTrailers(View view){
        Intent intent = new Intent(getApplicationContext(), TrailerActivity.class);
        intent.putExtra(getResources().getString(R.string.movie_id), currentMovie.getMovieId());
        startActivity(intent);
    }

    public void showReviews(View view){
        Intent intent = new Intent(getApplicationContext(), ReviewActivity.class);
        intent.putExtra(getResources().getString(R.string.movie_id), currentMovie.getMovieId());
        startActivity(intent);
    }


    private void getOtherItems(Movie movie) {
        isFavoriteMovie = FavoriteUtils.checkIfMovieIsFavorite(this, movie);
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
