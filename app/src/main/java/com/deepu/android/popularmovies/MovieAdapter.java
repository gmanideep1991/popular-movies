package com.deepu.android.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class MovieAdapter extends BaseAdapter {
    private Context mContext;
    private Movie[] movies;

    public MovieAdapter(Context mContext,Movie[] movies){
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
        if(convertView == null){
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setAdjustViewBounds(true);
        } else {
            imageView = (ImageView) convertView;
        }

        Picasso.get()
                .load(movies[position].getImageURL())
                .resize(mContext.getResources().getInteger(R.integer.poster_width),
                        mContext.getResources().getInteger(R.integer.poster_height))
                .placeholder(R.drawable.image_placeholder)
                .into(imageView);


        return imageView;
    }
}
