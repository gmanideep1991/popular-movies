package com.deepu.android.popularmovies.tasks;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.deepu.android.popularmovies.data.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GetReviewsAsyncTask extends AsyncTask<String, Void, Review[]> {

    private final String apiKey;
    private final OnReviewTaskCompleted onReviewTaskCompleted;

    public GetReviewsAsyncTask(String apiKey, OnReviewTaskCompleted onReviewTaskCompleted) {
        super();

        this.apiKey = apiKey;
        this.onReviewTaskCompleted = onReviewTaskCompleted;
    }

    @Override
    protected Review[] doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String reviewJsonStr = null;

        try {
            URL url = getApiUrl(params);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder builder = new StringBuilder();

            if (inputStream == null) {
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            if (builder.length() == 0) {
                // No data found. Nothing more to do here.
                return null;
            }

            reviewJsonStr = builder.toString();
        } catch (Exception e) {
            Log.e(GetReviewsAsyncTask.class.getSimpleName(), "Error calling TMDB", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(GetReviewsAsyncTask.class.getSimpleName(), "Error closing stream", e);
                }
            }
        }
        try {
            return getReviewsDataFromJson(reviewJsonStr);
        } catch (JSONException e) {
            Log.e(GetReviewsAsyncTask.class.getSimpleName(), e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    private URL getApiUrl(String[] parameters) throws MalformedURLException {
        final String TMDB_BASE_URL = "https://api.themoviedb.org/3/movie/";
        final String REVIEWS = "/reviews";
        final String API_KEY_PARAM = "api_key";

        Uri builtUri = Uri.parse(TMDB_BASE_URL + parameters[0] + REVIEWS + "?").buildUpon()
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .build();

        return new URL(builtUri.toString());
    }

    private Review[] getReviewsDataFromJson(String reviewsJsonStr) throws JSONException {
        // JSON tags
        final String RESULTS = "results";
        final String CONTENT = "content";
        final String AUTHOR = "author";

        JSONObject reviewsJson = new JSONObject(reviewsJsonStr);
        JSONArray resultsArray = reviewsJson.getJSONArray(RESULTS);

        Review[] reviews = new Review[resultsArray.length()];

        for (int i = 0; i < resultsArray.length(); i++) {
            reviews[i] = new Review();
            JSONObject reviewInfo = resultsArray.getJSONObject(i);
            reviews[i].setText(reviewInfo.getString(CONTENT));
            reviews[i].setAuthor(reviewInfo.getString(AUTHOR));
        }

        return reviews;
    }

    @Override
    protected void onPostExecute(Review[] reviews) {
        super.onPostExecute(reviews);
        onReviewTaskCompleted.onSearchReviewsTaskCompleted(reviews);
    }
}
