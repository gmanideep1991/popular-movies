package com.deepu.android.popularmovies.tasks;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.deepu.android.popularmovies.data.Trailer;

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

public class GetTrailersAsyncTask extends AsyncTask<String, Void, Trailer[]> {

    private final String apiKey;
    private final OnTrailerTaskCompleted onTrailerTaskCompleted;

    public GetTrailersAsyncTask(String apiKey, OnTrailerTaskCompleted onTrailerTaskCompleted) {
        super();

        this.apiKey = apiKey;
        this.onTrailerTaskCompleted = onTrailerTaskCompleted;
    }

    @Override
    protected Trailer[] doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String trailerJsonStr = null;

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

            trailerJsonStr = builder.toString();
        } catch (Exception e) {
            Log.e(GetTrailersAsyncTask.class.getSimpleName(), "Error calling TMDB", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(GetTrailersAsyncTask.class.getSimpleName(), "Error closing stream", e);
                }
            }
        }
        try {
            return getTrailersDataFromJson(trailerJsonStr);
        } catch (JSONException e) {
            Log.e(GetTrailersAsyncTask.class.getSimpleName(), e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    private URL getApiUrl(String[] parameters) throws MalformedURLException {
        final String TMDB_BASE_URL = "https://api.themoviedb.org/3/movie/";
        final String VIDEO = "/videos";
        final String API_KEY_PARAM = "api_key";

        Uri builtUri = Uri.parse(TMDB_BASE_URL + parameters[0] + VIDEO + "?").buildUpon()
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .build();

        return new URL(builtUri.toString());
    }

    private Trailer[] getTrailersDataFromJson(String trailersJsonStr) throws JSONException {
        // JSON tags
        final String RESULTS = "results";
        final String KEY = "key";

        JSONObject trailersJson = new JSONObject(trailersJsonStr);
        JSONArray resultsArray = trailersJson.getJSONArray(RESULTS);

        Trailer[] trailers = new Trailer[resultsArray.length()];

        for (int i = 0; i < resultsArray.length(); i++) {
            trailers[i] = new Trailer();
            JSONObject trailerInfo = resultsArray.getJSONObject(i);
            trailers[i].setTrailerKey(trailerInfo.getString(KEY));
        }

        return trailers;
    }

    @Override
    protected void onPostExecute(Trailer[] trailers) {
        super.onPostExecute(trailers);
        onTrailerTaskCompleted.onSearchTrailersTaskCompleted(trailers);
    }
}
