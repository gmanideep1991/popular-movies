package com.deepu.android.popularmovies.tasks;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.deepu.android.popularmovies.data.Movie;

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

public class GetMoviesAsyncTask extends AsyncTask<String, Void, Movie[]> {

    private final String apiKey;
    private final OnTaskCompleted onTaskCompleted;

    public GetMoviesAsyncTask(String apiKey, OnTaskCompleted onTaskCompleted) {
        super();

        this.apiKey = apiKey;
        this.onTaskCompleted = onTaskCompleted;
    }

    @Override
    protected Movie[] doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String moviesJsonStr = null;

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

            moviesJsonStr = builder.toString();
        } catch (Exception e) {
            Log.e(GetMoviesAsyncTask.class.getSimpleName(), "Error calling TMDB", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(GetMoviesAsyncTask.class.getSimpleName(), "Error closing stream", e);
                }
            }
        }
        try {
            return getMoviesDataFromJson(moviesJsonStr);
        } catch (JSONException e) {
            Log.e(GetMoviesAsyncTask.class.getSimpleName(), e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    private URL getApiUrl(String[] parameters) throws MalformedURLException {
        final String TMDB_BASE_URL = "https://api.themoviedb.org/3/movie/";
        final String API_KEY_PARAM = "api_key";

        Uri builtUri = Uri.parse(TMDB_BASE_URL + parameters[0] + "?").buildUpon()
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .build();

        return new URL(builtUri.toString());
    }

    private Movie[] getMoviesDataFromJson(String moviesJsonStr) throws JSONException {
        // JSON tags
        final String RESULTS = "results";
        final String TITLE = "title";
        final String ID = "id";
        final String POSTER_PATH = "poster_path";
        final String OVERVIEW = "overview";
        final String VOTE_AVERAGE = "vote_average";
        final String RELEASE_DATE = "release_date";

        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray resultsArray = moviesJson.getJSONArray(RESULTS);

        Movie[] movies = new Movie[resultsArray.length()];

        for (int i = 0; i < resultsArray.length(); i++) {
            movies[i] = new Movie();
            JSONObject movieInfo = resultsArray.getJSONObject(i);
            movies[i].setTitle(movieInfo.getString(TITLE));
            movies[i].setImageURL(movieInfo.getString(POSTER_PATH));
            movies[i].setSynopsis(movieInfo.getString(OVERVIEW));
            movies[i].setVoteAverage(movieInfo.getDouble(VOTE_AVERAGE));
            movies[i].setReleaseDate(movieInfo.getString(RELEASE_DATE));
            movies[i].setMovieId(movieInfo.getInt(ID));
        }

        return movies;
    }

    @Override
    protected void onPostExecute(Movie[] movies) {
        super.onPostExecute(movies);
        onTaskCompleted.onSearchMoviesTaskCompleted(movies);
    }
}
