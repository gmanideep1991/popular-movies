package com.deepu.android.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.deepu.android.popularmovies.R;
import com.deepu.android.popularmovies.activities.MovieDetailsActivity;
import com.deepu.android.popularmovies.data.Movie;
import com.deepu.android.popularmovies.utils.ImageUtils;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private Movie[] movies;
    private Context context;

    public MoviesAdapter(Movie[] movies,Context context) {
        this.movies = movies;
        this.context = context;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        LinearLayout view = (LinearLayout) layoutInflater.inflate(R.layout.movie_item, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        // Get corresponding review text
        final Movie movie = movies[position];

        if (movie != null) {
            ImageUtils.setImageView(movie,holder.poster);
            holder.poster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    launchDetailActivity(movie);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return movies.length;
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {

        private ImageView poster;

        public MovieViewHolder(View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.movie_poster);
        }

        public ImageView getPoster() {
            return poster;
        }
    }


    private void launchDetailActivity(Movie movie) {
        Intent intent = new Intent(context, MovieDetailsActivity.class);
        intent.putExtra(context.getResources().getString(R.string.parcel_movie), movie);
        context.startActivity(intent);
    }

}
