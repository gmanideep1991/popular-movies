package com.deepu.android.popularmovies.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.deepu.android.popularmovies.BuildConfig;
import com.deepu.android.popularmovies.R;
import com.deepu.android.popularmovies.adapters.MoviesAdapter;
import com.deepu.android.popularmovies.data.Movie;
import com.deepu.android.popularmovies.data.MovieContract;
import com.deepu.android.popularmovies.tasks.GetMoviesAsyncTask;
import com.deepu.android.popularmovies.tasks.OnTaskCompleted;
import com.deepu.android.popularmovies.utils.LoaderUtils;

import java.util.ArrayList;

import static com.deepu.android.popularmovies.utils.NetworkUtils.isInternetAvailable;

public class MainActivity extends AppCompatActivity {
    RecyclerView moviesView;
    LinearLayoutManager movieLayoutManager;
    private Movie[] movies;
    private Parcelable movieListState;
    private static Bundle mBundleState;
    private int mPosition = RecyclerView.NO_POSITION;
    private final String RECYCLER_POSITION_KEY = "recycler_position";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        moviesView = findViewById(R.id.movies_recycler_view);
        movieLayoutManager = new GridLayoutManager(this, 4);
        moviesView.setLayoutManager(movieLayoutManager);
        if(savedInstanceState == null){
            populateMovies();
        }

        else{
            Parcelable[] parcelables = savedInstanceState.getParcelableArray(getString(R.string.movies));
            if(parcelables !=null){
               // Movie[] movies = new Movie[parcelables.length];
                movies = new Movie[parcelables.length];
                for(int i =0; i< parcelables.length ;i++){
                    movies[i] = (Movie) parcelables[i];
                }
            }
            moviesView.setAdapter(new MoviesAdapter( movies,getApplicationContext()));

        }

    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        state.putParcelableArray(getString(R.string.movies), movies);
        movieListState = movieLayoutManager.onSaveInstanceState();
        state.putParcelable("movieState", movieListState);
        state.putInt(RECYCLER_POSITION_KEY,  movieLayoutManager.findFirstCompletelyVisibleItemPosition());
        super.onSaveInstanceState(state);

    }
    @Override
    protected void onRestoreInstanceState(Bundle state) {
        if (state.containsKey(RECYCLER_POSITION_KEY)) {
            mPosition = state.getInt(RECYCLER_POSITION_KEY);
            if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
            // Scroll the RecyclerView to mPosition
            moviesView.scrollToPosition(mPosition);
        }
        if(state != null){
            movieListState = state.getParcelable("movieState");
        }

        super.onRestoreInstanceState(state);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sort, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.most_popular:
                updateSharedPrefs(getString(R.string.tmdb_sort_pop_desc));
                getMovies(getSortMethod());
                return true;
            case R.id.highest_rated:
                updateSharedPrefs(getString(R.string.tmdb_sort_vote_avg_desc));
                getMovies(getSortMethod());
                return true;
            case R.id.favorites:
                updateSharedPrefs(getString(R.string.tmdb_sort_fav_desc));
                getFavoriteMovies();
                return true;
            default:
        }

        return super.onOptionsItemSelected(item);
    }

    private void getMovies(String sortMethod) {
        if (isInternetAvailable(this)) {
            String apiKey = BuildConfig.ApiKey;
            OnTaskCompleted taskCompleted = new OnTaskCompleted() {
                @Override
                public void onSearchMoviesTaskCompleted(Movie[] movies) {
                    updateReycleView(movies);

                }
            };
            GetMoviesAsyncTask getMoviesTask = new GetMoviesAsyncTask(apiKey, taskCompleted);
            getMoviesTask.execute(sortMethod);
        } else {
            updateSharedPrefs(getString(R.string.tmdb_sort_fav_desc));
            getFavoriteMovies();
            Toast.makeText(this, getString(R.string.switch_to_favorites), Toast.LENGTH_LONG).show();
        }
    }

    private void updateReycleView(Movie[] movies) {
        this.movies = movies;
        moviesView.setAdapter(new MoviesAdapter( movies,getApplicationContext()));
    }

    private String getSortMethod() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        return prefs.getString(getString(R.string.pref_sort_method_key),
                getString(R.string.tmdb_sort_pop_desc));
    }

    private void launchDetailActivity(Movie movie) {
        Intent intent = new Intent(getApplicationContext(), MovieDetailsActivity.class);
        intent.putExtra(getResources().getString(R.string.parcel_movie), movie);
        startActivity(intent);
    }

    private void updateSharedPrefs(String sortMethod) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.pref_sort_method_key), sortMethod);
        editor.apply();
    }

    private void getFavoriteMovies() {

        // Loader Manager
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> searchLoader = loaderManager.getLoader(LoaderUtils.FAVORITE_MOVIES_LOADER);

        if (searchLoader == null) {
            loaderManager.initLoader(LoaderUtils.FAVORITE_MOVIES_LOADER, null, new DatabaseMoviesLoader(this));
        } else {
            loaderManager.restartLoader(LoaderUtils.FAVORITE_MOVIES_LOADER, null, new DatabaseMoviesLoader(this));
        }
    }

    private class DatabaseMoviesLoader implements LoaderManager.LoaderCallbacks<Cursor> {

        private Context mContext;

        private DatabaseMoviesLoader(Context context) {
            mContext = context;
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {

            return new CursorLoader(mContext,
                    MovieContract.MovieEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    MovieContract.MovieEntry._ID);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

            if (data.getCount() > 0) {
                convertCursorIntoMoviesArray(data);
            } else {
                handleNoFavoriteMoviesSelected();
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            loader.cancelLoad();
        }
    }

    private void convertCursorIntoMoviesArray(Cursor cursor) {

        ArrayList<Movie> moviesDBArray = new ArrayList<Movie>();
        while (cursor.moveToNext()) {
            // Get movie data from cursor
            String movieId = LoaderUtils.getStringFromCursor(cursor,
                    MovieContract.MovieEntry.MOVIE_ID);
            String title = LoaderUtils.getStringFromCursor(cursor,
                    MovieContract.MovieEntry.TITLE);
            String releaseDate = LoaderUtils.getStringFromCursor(cursor, MovieContract.MovieEntry.RELEASE_DATE);
            String voteAverage = LoaderUtils.getStringFromCursor(cursor, MovieContract.MovieEntry.VOTE_AVERAGE);
            String synopsis = LoaderUtils.getStringFromCursor(cursor, MovieContract.MovieEntry.SYNOPSIS);
            byte[] poster = LoaderUtils.getBlob(cursor, MovieContract.MovieEntry.POSTER_IMAGE);
            Movie movie = new Movie();
            movie.setMovieId(Integer.parseInt(movieId));
            movie.setTitle(title);
            movie.setDbPosterImage(poster);
            movie.setVoteAverage(Double.parseDouble(voteAverage));
            movie.setReleaseDate(releaseDate);
            movie.setSynopsis(synopsis);
            moviesDBArray.add(movie);
        }

        movies = moviesDBArray.toArray(new Movie[moviesDBArray.size()]);
        moviesView.setAdapter(new MoviesAdapter( movies,getApplicationContext()));

    }

    private void handleNoFavoriteMoviesSelected() {
        updateSharedPrefs(getString(R.string.tmdb_sort_pop_desc));
        getMovies(getSortMethod());
        Toast.makeText(this, getString(R.string.switch_to_popular), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBundleState != null) {
            mPosition = mBundleState.getInt(RECYCLER_POSITION_KEY);
            if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
            moviesView.scrollToPosition(mPosition);
        }
        //populateMovies();
    }

    private void populateMovies(){
        if (getSortMethod().equals(getString(R.string.tmdb_sort_fav_desc))) {
            updateSharedPrefs(getString(R.string.tmdb_sort_fav_desc));
            getFavoriteMovies();
        } else {
            updateSharedPrefs(getSortMethod());
            getMovies(getSortMethod());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBundleState = new Bundle();
        mPosition = movieLayoutManager.findFirstCompletelyVisibleItemPosition();
        mBundleState.putInt(RECYCLER_POSITION_KEY, mPosition);
    }
}
