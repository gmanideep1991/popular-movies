package com.deepu.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridView = (GridView) findViewById(R.id.movie_grid);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                launchDetailActivity((Movie) parent.getItemAtPosition(position));
            }
        });

        if(savedInstanceState == null){
            getMovies(getSortMethod());
        }
        else{
            Parcelable[] parcelables = savedInstanceState.getParcelableArray(getString(R.string.parcel_movie));

            if(parcelables !=null){
                Movie[] movies = new Movie[parcelables.length];
                for(int i =0; i< parcelables.length ;i++){
                    movies[i] = (Movie) parcelables[i];
                }
                gridView.setAdapter(new MovieAdapter(this,movies));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sort,menu);
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
            default:
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        int numMovieObjects = gridView.getCount();
        if (numMovieObjects > 0) {
            Movie[] movies = new Movie[numMovieObjects];
            for (int i = 0; i < numMovieObjects; i++) {
                movies[i] = (Movie) gridView.getItemAtPosition(i);
            }
            outState.putParcelableArray(getString(R.string.parcel_movie), movies);
        }

        super.onSaveInstanceState(outState);
    }

    private void getMovies(String sortMethod){
        if(isInternetAvailable()){
            String apiKey = BuildConfig.ApiKey;
            OnTaskCompleted taskCompleted = new OnTaskCompleted() {
                @Override
                public void onSearchMoviesTaskCompleted(Movie[] movies) {
                    gridView.setAdapter(new MovieAdapter(getApplicationContext(), movies));
                }
            };
            GetMoviesAsyncTask getMoviesTask = new GetMoviesAsyncTask(apiKey, taskCompleted);
            getMoviesTask.execute(sortMethod);
        }
        else{
            Toast.makeText(this,getString(R.string.network_unavilable),Toast.LENGTH_LONG).show();
        }
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

    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void updateSharedPrefs(String sortMethod) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.pref_sort_method_key), sortMethod);
        editor.apply();
    }

}
