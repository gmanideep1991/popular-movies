package com.deepu.android.popularmovies.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.deepu.android.popularmovies.data.Movie;
import com.deepu.android.popularmovies.utils.ImageUtils;

public class MovieAdapter extends BaseAdapter {
    private Context mContext;
    private Movie[] movies;

    public MovieAdapter(Context mContext, Movie[] movies) {
        this.mContext = mContext;
        this.movies = movies;
    }

    @Override
    public int getCount() {
        return movies.length;
    }

    @Override
    public Object getItem(int i) {
        return movies[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setAdjustViewBounds(true);
        } else {
            imageView = (ImageView) convertView;
        }
        ImageUtils.setImageView(movies[position], imageView);
        return imageView;
    }
}
