package com.deepu.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        ImageView poster = (ImageView) findViewById(R.id.poster);
        TextView title = (TextView) findViewById(R.id.movie_title);
        TextView releaseDate = (TextView) findViewById(R.id.release_date);
        TextView voteAverage = (TextView) findViewById(R.id.vote_average);
        TextView synopsis = (TextView) findViewById(R.id.synopsis);

        Intent intent = getIntent();

        Movie movie = intent.getParcelableExtra(getString(R.string.parcel_movie));

        title.setText(movie.getTitle());
        Picasso.get().load(movie.getImageURL()).placeholder(R.drawable.image_placeholder)
                .into(poster);

        synopsis.setText(movie.getSynopsis());
        voteAverage.setText(movie.getRating());
        releaseDate.setText(movie.getReleaseDate());
    }
}
