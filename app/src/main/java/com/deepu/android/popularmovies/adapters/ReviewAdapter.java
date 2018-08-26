package com.deepu.android.popularmovies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.deepu.android.popularmovies.R;
import com.deepu.android.popularmovies.data.Review;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private Review[] reviews;

    public ReviewAdapter(Review[] reviews) {
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        LinearLayout view = (LinearLayout) layoutInflater.inflate(R.layout.review_item, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        // Get corresponding review text
        Review review = reviews[position];

        if (review != null) {
            holder.reviewAuthor.setText(review.getAuthor());
            holder.reviewText.setText(review.getText());
        }
    }

    @Override
    public int getItemCount() {
        return reviews.length;
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {

        private TextView reviewAuthor;
        private TextView reviewText;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            reviewAuthor = itemView.findViewById(R.id.review_author);
            reviewText = itemView.findViewById(R.id.review_text);

        }
    }

}
