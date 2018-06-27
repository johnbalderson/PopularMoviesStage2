package com.johnbalderson.popularmoviesstage2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.johnbalderson.popularmoviesstage2.db_utils.MovieDBLayout;
import com.johnbalderson.popularmoviesstage2.data_models.Movie;
import com.johnbalderson.popularmoviesstage2.adapters.MovieAdapter;

import com.johnbalderson.popularmoviesstage2.general_utils.JsonUtils;
import com.johnbalderson.popularmoviesstage2.general_utils.NetworkUtils;


import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("DanglingJavadoc")
public class MainActivity extends AppCompatActivity implements MovieAdapter.ListItemClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();


    private String mApiKey = "";
    
    // sort parameters
    private static final String SORT_POPULAR = "popular";
    private static final String SORT_TOP_RATED = "top_rated";
    private static final String FAVORITE = "Favorite";
    private static String currentSortType = SORT_POPULAR;
    
    private static final String MOVIE_ITEMS = "movie items";
    private static final String CURRENT_SORT = "current sort";
    
    private ArrayList<Movie> movieItems;

    private MovieAdapter mMovieAdapter;


    /**
     * This method checks network connection. This code was derived from
     * https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
     **/

    private boolean isOnline() {

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();

    }

    @Override
    public void OnListItemClick(Movie movieItem) {
        // when movie is clicked on, pass info to Details Activity
        Intent myIntent = new Intent(this, MovieDetailsActivity.class);
        myIntent.putExtra(MovieDetailsActivity.EXTRA_INDEX, 1);
        myIntent.putExtra("movieItem", movieItem);
        startActivity(myIntent);
    }

    /**
      Details on implementing saveInstanceState found here:
      https://stackoverflow.com/questions/6525698/how-to-use-onsavedinstancestate-example-please
      https://stackoverflow.com/questions/151777/saving-android-activity-state-using-save-instance-state
     */

    /**
     * Save movieItems and currentSort in SaveInstanceState bundle
    */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MOVIE_ITEMS, movieItems);
        outState.putString(CURRENT_SORT, currentSortType);
    }

    /**
     * Restore movie items and current sort
    */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        movieItems = savedInstanceState.getParcelableArrayList(MOVIE_ITEMS);
        currentSortType = savedInstanceState.getString(CURRENT_SORT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentSortType.equals(FAVORITE)) {
            ClearMovieItemList();
            LoadView();
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!isOnline()) {
            // if not online, show dialog box and end app
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.online_error)
                    .setCancelable(false)
                    .setTitle(R.string.online_error_title)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finishAffinity();
                            System.exit(0);
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

        // if API key not entered in gradle.properties, end app
        if (Objects.equals(BuildConfig.API_KEY, "YOUR API KEY")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.api_error)
                    .setCancelable(false)
                    .setTitle(R.string.api_error_title)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finishAffinity();
                            System.exit(0);
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } else {

            // get apiKey from gradle.properties
            mApiKey = BuildConfig.API_KEY;

            // set up movies DB to store favorites and movies
            // MovieDbHelper movieDbHelper = new MovieDbHelper(this);
            // SQLiteDatabase mDb = movieDbHelper.getWritableDatabase();

            //get reference to movie recycler view
            RecyclerView mMovieItemList = findViewById(R.id.rv_movies);
            GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
            mMovieItemList.setLayoutManager(layoutManager);
            mMovieItemList.setHasFixedSize(true);
            mMovieAdapter = new MovieAdapter(movieItems, this);
            mMovieItemList.setAdapter(mMovieAdapter);

            //if saved instance is not null and contains movie items, we will restore that instead.
            if (savedInstanceState != null && savedInstanceState.containsKey(MOVIE_ITEMS)) {
                movieItems = savedInstanceState.getParcelableArrayList(MOVIE_ITEMS);
                currentSortType = savedInstanceState.getString(CURRENT_SORT, SORT_POPULAR);
            }
            LoadView();
        }
    }

        /**
         * This method loads the main view. It first checks if the movieItems is empty or null and in that case
         * calls setMovieData to get data, otherwise, just load the view from existing list of movie items.
         */
        private void LoadView() {
            setTitle(R.string.app_name);
            if (movieItems == null || movieItems.isEmpty()) {
                if (currentSortType.equals(FAVORITE)) {
                        new DatabaseQueryTask().execute();
                } else {
                    new NetworkQueryTask().execute(NetworkUtils.buildDataURL
                            (mApiKey, currentSortType));
                }
            } else {
                mMovieAdapter.setMovieData(movieItems);
            }
        }




   //  Async task to fetch data from network and new data is applied to adapter.

    @SuppressLint("StaticFieldLeak")
    class NetworkQueryTask extends AsyncTask<URL, Void, String> {
            @Override
            protected String doInBackground(URL... params) {
                URL searchUrl = params[0];
                String searchResults = null;
                try {
                    searchResults = NetworkUtils.getResponseFromHttpURL(searchUrl);
                } catch (IOException e) {
                    Log.i(TAG, e.toString());
                    e.printStackTrace();
                }
                return searchResults;
            }

            @Override
            protected void onPostExecute(String searchResults) {
                if (searchResults != null && !searchResults.equals("")) {
                    movieItems = JsonUtils.parseMovieJson(searchResults);
                    updateFavoriteItems();
                    mMovieAdapter.setMovieData(movieItems);
                } else {
                    Toast.makeText(getApplicationContext(),
                            R.string.API_ERROR, Toast.LENGTH_LONG).show();
                }
            }
        }

    @SuppressLint("StaticFieldLeak")
    class DatabaseQueryTask extends AsyncTask<Void, Void, List<Movie>> {
        @Override
        protected List<Movie> doInBackground(Void... voids) {
            List<Movie> searchResults = null;
            try {
                searchResults = getAllFavoriteMovies();
            } catch (Exception e) {
                Log.i(TAG, e.toString());
                e.printStackTrace();
            }
            return searchResults;
        }

        @Override
        protected void onPostExecute(List<Movie> searchResults) {
            if (searchResults != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                // if on Favorites page change title so user will know they are on the favorites page
                // title will be reset once user clicks back arrow or changes sort parameter

                setTitle(R.string.favorites);
                mMovieAdapter.setMovieData(searchResults);
            } else {
                Toast.makeText(getApplicationContext(),
                        R.string.db_error, Toast.LENGTH_LONG).show();
            }
        }
    }


        // This method checks if movie is also in favorite list and marks the list accordingly.

        private void updateFavoriteItems() {

            ArrayList<Movie> favoriteMovieItems = getAllFavoriteMovies();

            for (int j = 0; j < movieItems.size(); j++) {
                Movie item = movieItems.get(j);
                for (int i = 0; i < favoriteMovieItems.size(); i++) {
                    Movie temp = favoriteMovieItems.get(i);
                    if (temp.getMovieId().equals(item.getMovieId())) {
                        item.setMovieFavorite(true);
                        movieItems.set(j, item);
                        break;
                    }
                }
            }
        }

        // clear the movie list
        private void ClearMovieItemList() {
            if (movieItems != null) {
                movieItems.clear();
                mMovieAdapter.notifyDataSetChanged();
            }
        }

        // get the list of favorite movies
        private ArrayList<Movie> getAllFavoriteMovies() {
            ArrayList<Movie> result = new ArrayList<>();
            try (Cursor cursor = getContentResolver().query(MovieDBLayout.MovieTable.CONTENT_URI,
                    null,
                    null,
                    null,
                    MovieDBLayout.MovieTable.COLUMN_VOTE_AVERAGE)) {
                while (cursor.moveToNext()) {

                    // add movie to favorites list, set favorite flag to true
                    result.add(new Movie(
                      cursor.getString(cursor.getColumnIndex(MovieDBLayout.MovieTable.COLUMN_ID)),
                      cursor.getString(cursor.getColumnIndex(MovieDBLayout.MovieTable.COLUMN_ORIGINAL_TITLE)),
                      cursor.getString(cursor.getColumnIndex(MovieDBLayout.MovieTable.COLUMN_SYNOPSIS)),
                      cursor.getString(cursor.getColumnIndex(MovieDBLayout.MovieTable.COLUMN_POSTER_PATH)),
                      cursor.getString(cursor.getColumnIndex(MovieDBLayout.MovieTable.COLUMN_RELEASE_DATE)),
                      cursor.getDouble(cursor.getColumnIndex(MovieDBLayout.MovieTable.COLUMN_VOTE_AVERAGE)),
                      true
                    ));
                }
            }
            return result;
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort_menu, menu);
        return true;
    }


     // Depending on the item selected, this method sets the current sort and clears the movie item list.

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // check if back arrow was pressed
        if (id == android.R.id.home) {
            onBackPressed();
            finish();
            return true;
        }

        /* if menu option is popular and current sort parameter is not popular, then set current sort
            parameter to popular
         */
        if (id == R.id.popular_sort && !currentSortType.equals(SORT_POPULAR)) {
            ClearMovieItemList();
            currentSortType = SORT_POPULAR;
            LoadView();
            return true;
        }

          /* if menu option is top rated movies and current sort parameter is not top rated, then set current sort
            parameter to top rated
         */
        if (id == R.id.top_rated_sort && !currentSortType.equals(SORT_TOP_RATED)) {
            ClearMovieItemList();
            currentSortType = SORT_TOP_RATED;
            LoadView();
            return true;
        }

          /* if menu option is favorite and current sort parameter is not favorite, then set current sort
            parameter to favorite
         */
        if (id == R.id.favorites && !currentSortType.equals(FAVORITE)) {
            ClearMovieItemList();
            currentSortType = FAVORITE;
            LoadView();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }








}










