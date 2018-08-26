package com.deepu.android.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.deepu.android.popularmovies.R;
import com.deepu.android.popularmovies.data.Trailer;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private Trailer[] trailers;
    private Context context;

    public TrailerAdapter(Trailer[] trailers,Context context) {
        this.trailers = trailers;
        this.context = context;
    }

    //Launch trailer
    private static final String YOUTUBE_BASE_PATH = "https://www.youtube.com/watch?v=";

    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        LinearLayout view = (LinearLayout) layoutInflater.inflate(R.layout.trailer_item, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder holder, int position) {
        // Get corresponding review text
        final Trailer trailer = trailers[position];

        if (trailer != null) {
            holder.trailerName.setText("Watch Trailer " + (position + 1));
            holder.trailerName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    launchTrailer(context, trailer.getTrailerKey());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return trailers.length;
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder {

        private Button trailerName;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            trailerName = itemView.findViewById(R.id.trailer_text);

        }
    }

    private static void launchTrailer(Context context, String trailerKey) {
        Uri youtubeLink = Uri.parse(YOUTUBE_BASE_PATH + trailerKey);
        Intent intent = new Intent(Intent.ACTION_VIEW, youtubeLink);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

}
